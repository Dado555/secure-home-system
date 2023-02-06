package rs.securehome.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rs.securehome.admin.annotation.Log;
import rs.securehome.admin.model.User;
import rs.securehome.admin.service.UserService;
import rs.securehome.admin.util.jwt.JwtUtil;
import rs.securehome.common.dto.*;

import javax.security.auth.login.AccountLockedException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/users")
@Slf4j
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil tokenUtil;
    private final UserService userService;

    @Value("${jwt.header.string}")
    public String headerString;

    @Value("${jwt.token.prefix}")
    public String tokenPrefix;


    @Autowired
    public UserController(AuthenticationManager authenticationManager, JwtUtil tokenUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.tokenUtil = tokenUtil;
        this.userService = userService;
    }

    @Log(message = "Change default password.")
    @PutMapping(value = "/{id}/change-default-password")
    public ResponseEntity<String> changeDefaultPassword(HttpServletRequest request, @PathVariable("id") Integer id, @Valid @RequestBody ChangeDefaultPasswordRequestDTO changeDefaultPasswordRequest) {
        //TODO: mozda dodati neki aspekt da provjerava da li je sifra promijenjena prilikom svakog zahtjeva
        this.userService.changeDefaultPassword(id, changeDefaultPasswordRequest.getOldPassword(), changeDefaultPasswordRequest.getNewPassword());
        var headers = new HttpHeaders();
        headers.add("Set-Cookie", "jwt-security=0" + "; SameSite=Strict; Secure; HttpOnly; Path=/api; expires=Thu, 01 Jan 1970 00:00:00 GMT");
        return new ResponseEntity<>("Password successfully changed.", headers, HttpStatus.OK);

    }


    @Log(message = "Logout")
    @GetMapping(value = "/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader(headerString);
        String jwt = authorizationHeader.replace(tokenPrefix, "");
        this.userService.logout(jwt);
        var headers = new HttpHeaders();
        headers.add("Set-Cookie", "jwt-security=0" + "; SameSite=Strict; Secure; HttpOnly; Path=/api; expires=Thu, 01 Jan 1970 00:00:00 GMT");
        return new ResponseEntity<>("Logout successful.", headers, HttpStatus.OK);

    }

    @Log(message = "Login")
    @PostMapping(value = "/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(HttpServletRequest request, @RequestBody @Valid AuthenticationRequestDTO authenticationRequestDTO, HttpServletResponse response) throws AccountLockedException {
        this.userService.checkForFailedLoginAttempts(authenticationRequestDTO.getEmail(), authenticationRequestDTO.getPassword(), request);
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequestDTO.getEmail(), authenticationRequestDTO.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtSecurity = tokenUtil.generateJWTSecurity();
        String token = tokenUtil.generateToken(authentication, jwtSecurity);

        var headers = new HttpHeaders();
        headers.add("Set-Cookie", "jwt-security=" + jwtSecurity + "; SameSite=Strict; Secure; HttpOnly; Path=/api");
        return new ResponseEntity<>(new AuthenticationResponseDTO(token), headers, HttpStatus.OK);
    }

    @Log(message = "Create a new tenant.")
    @PostMapping("/tenant")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO createTenant(HttpServletRequest request, @RequestBody CreateTenantDTO tenantDTO) {
        var tenant = new User(tenantDTO.getEmail(), tenantDTO.getPassword(), tenantDTO.getName(), tenantDTO.getSurname(), null);
        var createdUser = userService.createNewTenant(tenant, tenantDTO.getHouseholdID());
        return new UserResponseDTO(createdUser.getId(), createdUser.getEmail(), createdUser.getName(), createdUser.getSurname(), createdUser.getAuthority().getName());
    }

    @Log(message = "Update tenant with a given id.")
    @PutMapping("/tenant/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTenant(HttpServletRequest request, @RequestBody UpdateTenantDTO tenantDTO, @PathVariable int id) {
        var tenant = new User("", "", tenantDTO.getName(), tenantDTO.getSurname(), null);
        userService.updateTenant(id, tenant);
    }

    @Log(message = "Fetch all users.")
    @GetMapping
    public Page<UserResponseDTO> getAllUsers(HttpServletRequest request, Pageable pageable) {
        return userService.getAllUsers(pageable).map(u -> new UserResponseDTO(u.getId(), u.getEmail(), u.getName(), u.getSurname(), u.getAuthority().getName()));
    }

    @Log(message = "Fetch all users for a household with a given id in pages.")
    @GetMapping("/household/{id}")
    public Page<UserResponseDTO> getAllUsersForHousehold(HttpServletRequest request, Pageable pageable, @PathVariable Integer id) {
        return userService.getAllUsersForHousehold(pageable, id).map(u -> new UserResponseDTO(u.getId(), u.getEmail(), u.getName(), u.getSurname(), u.getAuthority().getName()));
    }

    @Log(message = "Fetch all users for a household with a given id as a list.")
    @GetMapping("/household-id/{id}")
    public List<UserResponseDTO> getAllUsersForHouseholdId(HttpServletRequest request, @PathVariable Integer id) {
        return userService.getAllUsersByHouseholdId(id);
    }

    @Log(message = "Upgrade role of a tenant with a given id.")
    @PutMapping("/tenant/role/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void upgradeTenantRole(HttpServletRequest request, @PathVariable Integer id) {
        userService.upgradeTenantRole(id);
    }

    @Log(message = "Update tenant role.")
    @PutMapping("/tenant/role")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTenantRole(HttpServletRequest request, @RequestBody UpdateTenantRoleDTO updateRoleDTO) {
        userService.updateTenantRole(updateRoleDTO.getId(), updateRoleDTO.getRole());
    }

    @Log(message = "Delete user with a given id.")
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(HttpServletRequest request, @PathVariable int userId) {
        userService.deleteUser(userId);
    }

}
