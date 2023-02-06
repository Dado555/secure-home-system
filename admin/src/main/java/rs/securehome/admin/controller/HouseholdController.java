package rs.securehome.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import rs.securehome.admin.annotation.Log;
import rs.securehome.admin.service.HouseholdService;
import rs.securehome.common.dto.HouseholdResponseDTO;
import rs.securehome.common.dto.UserResponseDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@RestController
@RequestMapping(value = "/api/households")
public class HouseholdController {

    private final HouseholdService householdService;

    @Autowired
    public HouseholdController(HouseholdService householdService) {
        this.householdService = householdService;
    }

    @Log(message = "Fetch all households.")
    @GetMapping
    public Page<HouseholdResponseDTO> getAllHouseholds(HttpServletRequest request, Pageable pageable){
        return householdService.getAllHouseholds(pageable).map((household) -> {
            var householdResponse = new HouseholdResponseDTO();
            householdResponse.setId(household.getId());
            for(var tenant : household.getTenants()) {
                if(tenant.getAuthority().getName().equals("HOUSE_HEAD")) {
                    householdResponse.getHouseHeads().add(new UserResponseDTO(tenant.getId(), tenant.getEmail(), tenant.getName(), tenant.getSurname(), tenant.getAuthority().getName()));
                }
            }
            return householdResponse;
        });
    }

    @Log(message = "Delete a household with a given id.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHousehold(HttpServletRequest request, @PathVariable Integer id) {
        householdService.deleteHousehold(id);
    }

    @Log(message = "Fetch households for an user with a given id.")
    @GetMapping("/user/{id}")
    public HouseholdResponseDTO getHouseholdForUser(HttpServletRequest request, @PathVariable Integer id) {
        var household = householdService.getHouseholdForUser(id);
        return new HouseholdResponseDTO(household.getId(), new ArrayList<>()); // ne treba mi ova lista u funkcionalnostima pa je samo ovako vracam
    }
}
