package rs.securehome.admin.service;

import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rs.securehome.admin.exception.*;
import rs.securehome.admin.model.BlacklistedJWT;
import rs.securehome.admin.model.FailedLoginAttempt;
import rs.securehome.admin.model.User;
import rs.securehome.admin.model.events.Alarm;
import rs.securehome.admin.model.events.AlarmType;
import rs.securehome.admin.model.events.LoginFailed;
import rs.securehome.admin.repository.*;
import rs.securehome.admin.util.PasswordGenerator;
import rs.securehome.common.dto.UserResponseDTO;

import javax.security.auth.login.AccountLockedException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BlacklistedJWTRepository blacklistedJWTRepository;
    private final PasswordEncoder passwordEncoder;
    private final FailedLoginAttemptRepository failedLoginAttemptRepository;
    private final HouseholdRepository householdRepository;
    private final AuthorityRepository authorityRepository;
    private final RealEstateRepository realEstateRepository;
    private final KieSession kieSession;
    private final AlarmRepository alarmRepository;

    @Autowired
    public UserService(UserRepository userRepository, BlacklistedJWTRepository blacklistedJWTRepository,
                       PasswordEncoder passwordEncoder, FailedLoginAttemptRepository failedLoginAttemptRepository,
                       HouseholdRepository householdRepository, AuthorityRepository authorityRepository,
                       RealEstateRepository realEstateRepository, KieSession kieSession, AlarmRepository alarmRepository) {
        this.userRepository = userRepository;
        this.blacklistedJWTRepository = blacklistedJWTRepository;
        this.passwordEncoder = passwordEncoder;
        this.failedLoginAttemptRepository = failedLoginAttemptRepository;
        this.householdRepository = householdRepository;
        this.authorityRepository = authorityRepository;
        this.realEstateRepository = realEstateRepository;
        this.kieSession = kieSession;
        this.alarmRepository = alarmRepository;
    }

    public Optional<User> findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public void logout(String jwt) {
        this.blacklistedJWTRepository.save(new BlacklistedJWT(jwt));
    }

    public void checkForFailedLoginAttempts(String email, String password, HttpServletRequest request) throws AccountLockedException {
        var userByEmail = this.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Bad credentials"));

        if (!passwordEncoder.matches(password, userByEmail.getPassword())) {
            //dobar username, los password
            //ako je broj neuspjelih prijavljivanja veci ili jednak 5 u zadnjih 3 minuta, zakljucaj nalog

            // Neuspešni pokušaji prijave na sistem sa istim korisničkim imenom
            // CEP - alarm
            var now = LocalDateTime.now();

            LoginFailed lf = new LoginFailed(email, now);
            kieSession.insert(lf);
            kieSession.fireAllRules();

            alarmRepository.save(new Alarm(AlarmType.LOGIN_FAIL, lf, LocalDateTime.now()));

            var userId = userByEmail.getId();

            var failedLoginAttempt = new FailedLoginAttempt(userByEmail, now);
            this.failedLoginAttemptRepository.save(failedLoginAttempt);

            int numberOfFailedLoginAttempts = this.failedLoginAttemptRepository.countAttemptsInPeriodForUser(userId, now.minusMinutes(3L), now);
            if (numberOfFailedLoginAttempts >= 5) {
                var lockedUser = this.lockUserAccount(userId);
                throw new AccountLockedException(String.format("Account has been locked due to great number of failed login attempts. Lock expires at %s.", lockedUser.getAccountLockedAt().plusDays(User.ACCOUNT_LOCK_DURATION_DAYS)));
            }
        }
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with email: %s not found.", username)));
    }

    private User findById(Integer userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id: %d not found.", userId)));
    }

    public void changeDefaultPassword(Integer userId, String oldPassword, String newPassword) {
        var user = this.findById(userId);
        if(Boolean.TRUE.equals(user.getDefaultPasswordChanged()))
            throw new PasswordException("Default password has already been changed.");
        if(!passwordEncoder.matches(oldPassword, user.getPassword()))
            throw new PasswordException("Old password is not correct.");
        if(!PasswordGenerator.validatePasswordStrength(newPassword))
            throw new PasswordFailedPolicyCheckException("New password is not following the best practice policy.");
        if(oldPassword.equals(newPassword))
            throw new PasswordException("New password cannot be the same as the old password.");
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setDefaultPasswordChanged(true);
        this.userRepository.save(user);
    }

    public User lockUserAccount(Integer userId) {
        var user = this.findById(userId);
        user.setAccountLockedAt(LocalDateTime.now());
        return this.userRepository.save(user);
    }

    public User createNewTenant(User tenant, int householdId) {
        var household = householdRepository.findById(householdId).orElseThrow(() -> new NotFoundException("Household with that id does not exist"));

        tenant.setAuthority(authorityRepository.findByName("HOUSE_TENANT"));

        if(!PasswordGenerator.validatePasswordStrength(tenant.getPassword())) {
            throw new PasswordFailedPolicyCheckException("Password is not following the best practice policy.");
        }

        tenant.setPassword(passwordEncoder.encode(tenant.getPassword()));
        var createdTenant = userRepository.save(tenant);

        household.addTenant(tenant);
        householdRepository.save(household);
        return createdTenant;
    }

    public void updateTenant(int id, User tenant) {
        var tenantToUpdate = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with id: " + id + " does not exist."));

        var currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!userRepository.getTenantsForHouseHead(currentUser.getId()).contains(tenantToUpdate)) {
            throw new TenantNotInHouseholdException("You can not update tenant that is not in your household.");
        }

        tenantToUpdate.setName(tenant.getName());
        tenantToUpdate.setSurname(tenant.getSurname());

        userRepository.save(tenantToUpdate);
    }

    public void updateTenantRole(int id, String role) {
        var tenantToUpdate = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with id: " + id + " does not exist."));

        tenantToUpdate.setAuthority(authorityRepository.findByName(role));

        userRepository.save(tenantToUpdate);

        if(role.equals("HOUSE_HEAD")) {
            allowTenantToSeeEverything(tenantToUpdate);
        }
    }

    public void upgradeTenantRole(int id) {
        var tenantToUpdate = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with id: " + id + " does not exist."));

        var currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!userRepository.getTenantsForHouseHead(currentUser.getId()).contains(tenantToUpdate)) {
            throw new TenantNotInHouseholdException("You can not update tenant that is not in your household.");
        }

        tenantToUpdate.setAuthority(authorityRepository.findByName("HOUSE_HEAD"));

        userRepository.save(tenantToUpdate);

        allowTenantToSeeEverything(tenantToUpdate);
    }

    private void allowTenantToSeeEverything(User tenant) {
        var realEstate = realEstateRepository.getAllForHousehold(tenant.getHousehold().getId());
        for(var re : realEstate) {
            re.getCanBeSeenBy().add(tenant);
            realEstateRepository.save(re);
        }
    }

    private void deleteUserFromEverything(User tenant) {
        var realEstate = realEstateRepository.getAllForHousehold(tenant.getHousehold().getId());
        for(var re : realEstate) {
            re.getCanBeSeenBy().remove(tenant);
            realEstateRepository.save(re);
        }
    }

    public void deleteUser(int id) {
        var userToDelete = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with id: " + id + " does not exist."));

        if(userToDelete.getAuthority().getName().equals("ADMIN")) {
            throw new UserIsAdminException("User with id: " + id + " is admin and you cannot delete him.");
        }

        deleteUserFromEverything(userToDelete);

        userRepository.delete(userToDelete);
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<User> getAllUsersForHousehold(Pageable pageable, int householdId) {
        return userRepository.getAllUsersForHousehold(pageable, householdId);
    }

    public List<UserResponseDTO> getAllUsersByHouseholdId(int householdId) {
        return userRepository.getAllByHouseholdId(householdId).stream()
                .map((u) -> new UserResponseDTO(u.getId(), u.getEmail(), u.getName(), u.getSurname(), u.getAuthority().getName()))
                .collect(Collectors.toList());
    }
}
