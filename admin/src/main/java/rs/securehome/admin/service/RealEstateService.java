package rs.securehome.admin.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rs.securehome.admin.exception.CantRemoveAnotherHouseHead;
import rs.securehome.admin.exception.NotFoundException;
import rs.securehome.admin.exception.TenantNotInHouseholdException;
import rs.securehome.admin.model.Household;
import rs.securehome.admin.model.RealEstate;
import rs.securehome.admin.model.User;
import rs.securehome.admin.repository.HouseholdRepository;
import rs.securehome.admin.repository.RealEstateRepository;
import rs.securehome.admin.repository.UserRepository;
import rs.securehome.common.dto.RealEstateCreateDTO;
import rs.securehome.common.dto.RealEstateUpdateDTO;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class RealEstateService {

    private final RealEstateRepository realEstateRepository;
    private final HouseholdRepository householdRepository;
    private final UserRepository userRepository;

    public RealEstateService(RealEstateRepository realEstateRepository,
                             HouseholdRepository householdRepository,
                             UserRepository userRepository) {
        this.realEstateRepository = realEstateRepository;
        this.householdRepository = householdRepository;
        this.userRepository = userRepository;
    }

    public Set<RealEstate> getRealEstateForTenant(int userId) {
        var household = householdRepository.getHouseholdForUser(userId).orElseThrow(() -> new NotFoundException("Household for user with id: " + userId + " not found."));
        var householdRealEstate = realEstateRepository.getAllForHousehold(household.getId());

        Set<RealEstate> toRet = new HashSet<>();
        for(var re : householdRealEstate) {
            for(var u : re.getCanBeSeenBy()) {
                if(u.getId() == userId) {
                    toRet.add(re);
                }
            }
        }

        return toRet;
    }

    public Page<RealEstate> getAllForHousehold(Pageable pageable, int householdId) {
        return realEstateRepository.getAllForHouseholdPageable(pageable, householdId);
    }

    public RealEstate getRealEstate(int id) {
        return realEstateRepository.findById(id).orElseThrow(() -> new NotFoundException("Real Estate with id: " + id + " not found."));
    }

    @Transactional
    public void setVisibilityForRealEstate(int[] userIds, int reId, String type) {
        var realEstate = realEstateRepository.getWithHousehold(reId).orElseThrow(() -> new NotFoundException("Real Estate with id: " + reId + " not found."));
        // realEstate.getCanBeSeenBy().clear();

        boolean flag, deletingHouseHead;
        for(int userId : userIds) {
            flag = false;
            deletingHouseHead = false;
            for(var user : realEstate.getHousehold().getTenants()) {
                if(user.getId() == userId) {
                    if(user.getAuthority().getName().equals("HOUSE_HEAD")) {
                        deletingHouseHead = true;
                    } else {
                        flag = true;
                        if(type.equals("add"))
                            realEstate.getCanBeSeenBy().add(userRepository.findById(userId).get());
                        else
                            realEstate.getCanBeSeenBy().remove(userRepository.findById(userId).get()); // .add(userRepository.getById(userId));
                    }
                }
            }
            if(!flag) {
                throw new TenantNotInHouseholdException("Tenant with id: " + userId + " does not own this real estate.");
            }
            if(deletingHouseHead) {
                throw new CantRemoveAnotherHouseHead("HouseHead (with id: " + userId + ") can't be removed.");
            }
        }
    }

    @Transactional
    public void createRealEstate(RealEstateCreateDTO realEstateCreateDTO) {
        var houseHold = householdRepository.findById(realEstateCreateDTO.getHouseholdId()).orElseThrow(() -> new NoSuchElementException("House hold with id: " + realEstateCreateDTO.getHouseholdId() + " not found."));
        RealEstate re = new RealEstate(realEstateCreateDTO.getName());
        populateRealEstateData(re, houseHold, realEstateCreateDTO.getCanBeSeenByIds(), realEstateCreateDTO.getReadingPeriod());
    }

    @Transactional
    public void updateRealEstate(RealEstateUpdateDTO realEstateUpdateDTO) {
        var realEstate = realEstateRepository.findById(realEstateUpdateDTO.getId()).orElseThrow(() -> new NoSuchElementException("Real estate with id: "+ realEstateUpdateDTO.getId() + " not found."));
        var houseHold = householdRepository.findById(realEstateUpdateDTO.getHouseholdId()).orElseThrow(() -> new NoSuchElementException("House hold with id: " + realEstateUpdateDTO.getHouseholdId() + " not found."));
        populateRealEstateData(realEstate, houseHold, realEstateUpdateDTO.getCanBeSeenByIds(), realEstateUpdateDTO.getReadingPeriod());
    }

    @Transactional
    public void updateRealEstateName(Integer id, String newName) {
        var realEstate = realEstateRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Real estate with id: "+ id + " not found."));
        realEstate.setName(newName);
        realEstateRepository.save(realEstate);
    }

    private void populateRealEstateData(RealEstate realEstate, Household houseHold, int[] canBeSeenByIds, Integer readingPeriod) {
        realEstate.setHousehold(houseHold);

        Set<User> canBeSeenBy = new HashSet<>();
        for(int id : canBeSeenByIds) {
            var user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with id: " + id + " not found."));
            canBeSeenBy.add(user);
        }
        realEstate.setCanBeSeenBy(canBeSeenBy);
        if(readingPeriod > 5)
            realEstate.setDeviceReadingPeriod(readingPeriod);

        realEstateRepository.save(realEstate);
    }

    @Transactional
    public void deleteRealEstate(int id) {
        var toDelete = realEstateRepository.findById(id).orElseThrow(() -> new NotFoundException("Real estate with id: " + id + " not found."));
        toDelete.getCanBeSeenBy().clear();

        realEstateRepository.delete(toDelete);
    }
}
