package rs.securehome.admin.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import rs.securehome.admin.annotation.Log;
import rs.securehome.admin.model.RealEstate;
import rs.securehome.admin.service.RealEstateService;
import rs.securehome.common.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/real-estate")
public class RealEstateController {

    private final RealEstateService realEstateService;

    public RealEstateController(RealEstateService realEstateService) {
        this.realEstateService = realEstateService;
    }

    @Log(message = "Fetch a real estate with a given id.")
    @GetMapping("/{id}")
    public RealEstateResponseDTO getSingleRealEstate(HttpServletRequest request, @PathVariable Integer id) {
        var realEstate = realEstateService.getRealEstate(id);
        return toDTO(realEstate);
    }

    @Log(message = "Fetch all real estates for a tenant.")
    @GetMapping("/tenant/{id}")
    public Set<RealEstateResponseDTO> getAllForUser(HttpServletRequest request, @PathVariable Integer id) {
        return realEstateService.getRealEstateForTenant(id).stream().map(this::toDTO).collect(Collectors.toSet());
    }

    @Log(message = "Fetch all real estates for a household with a given id.")
    @GetMapping("/household/{id}")
    public Page<RealEstateResponseDTO> getAllForHousehold(HttpServletRequest request, Pageable pageable, @PathVariable Integer id) {
        return realEstateService.getAllForHousehold(pageable, id).map(this::toDTO);
    }

    @Log(message = "Set visibility for a real estate.")
    @PutMapping("/visibility/{type}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setVisibilityForRealEstate(HttpServletRequest request, @RequestBody RealEstateVisibilityRequestDTO visibilityRequest, @PathVariable String type) {
        realEstateService.setVisibilityForRealEstate(visibilityRequest.getUserIds(), visibilityRequest.getRealEstateId(), type);
    }

    @Log(message = "Delete real estate with a given id.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRealEstate(HttpServletRequest request, @PathVariable Integer id) {
        realEstateService.deleteRealEstate(id);
    }

    private RealEstateResponseDTO toDTO(RealEstate re) {
        Set<UserResponseDTO> users = new HashSet<>();
        for (var user : re.getCanBeSeenBy()) {
            users.add(new UserResponseDTO(user.getId(), user.getEmail(), user.getName(), user.getSurname(), user.getAuthority().getName()));
        }
        return new RealEstateResponseDTO(re.getId(), re.getName(), users, re.getDeviceReadingPeriod());
    }

    @Log(message = "Create a new real estate.")
    @PostMapping("/create")
    public void createNewRealEstate(HttpServletRequest request, @RequestBody RealEstateCreateDTO realEstateCreateDTO) {
        realEstateService.createRealEstate(realEstateCreateDTO);
    }

    @Log(message = "Update real estate.")
    @PutMapping("/update")
    public void updateRealEstate(HttpServletRequest request, @RequestBody RealEstateUpdateDTO realEstateUpdateDTO) {
        realEstateService.updateRealEstate(realEstateUpdateDTO);
    }

    @Log(message = "Update real estate name.")
    @PutMapping("/update-name")
    public void updateRealEstateName(HttpServletRequest request, @RequestBody RealEstateUpdateDTO realEstateUpdateDTO) {
        realEstateService.updateRealEstateName(realEstateUpdateDTO.getId(), realEstateUpdateDTO.getName());
    }
}
