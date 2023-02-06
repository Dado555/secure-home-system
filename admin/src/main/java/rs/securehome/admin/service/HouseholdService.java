package rs.securehome.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rs.securehome.admin.exception.HouseholdNotEmptyException;
import rs.securehome.admin.exception.NotFoundException;
import rs.securehome.admin.model.Household;
import rs.securehome.admin.repository.HouseholdRepository;
import rs.securehome.admin.repository.UserRepository;

import java.util.NoSuchElementException;

@Service
public class HouseholdService {

    private final HouseholdRepository householdRepository;
    private final UserRepository userRepository;

    @Autowired
    public HouseholdService(HouseholdRepository householdRepository,
                            UserRepository userRepository) {
        this.householdRepository = householdRepository;
        this.userRepository = userRepository;
    }

    public void makeNewHousehold(int houseHeadUserId) {
        var houseHead = userRepository.findById(houseHeadUserId).orElseThrow(() -> new NoSuchElementException("User with id: " + houseHeadUserId + " not found."));
        var newHousehold = new Household();
        newHousehold.addTenant(houseHead);

        householdRepository.save(newHousehold);
        userRepository.save(houseHead);
    }

    public Page<Household> getAllHouseholds(Pageable pageable) {
        return householdRepository.findAll(pageable);
    }

    public void deleteHousehold(int id) {
        var toDelete = householdRepository.findById(id).orElseThrow(() -> new NotFoundException("Household with id: " + id + " does not exist."));
        if(toDelete.getTenants().size() > 0) {
            throw new HouseholdNotEmptyException("Household with id: " + id + " is not empty.");
        }

        householdRepository.delete(toDelete);
    }

    public Household getHouseholdForUser(int id) {
        return householdRepository.getHouseholdForUser(id).orElseThrow(() -> new NotFoundException("Household for user with id: " + id + " does not exist."));
    }
}
