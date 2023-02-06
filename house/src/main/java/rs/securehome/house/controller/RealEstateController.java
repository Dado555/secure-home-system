package rs.securehome.house.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import rs.securehome.common.dto.RealEstateCreateDTO;
import rs.securehome.common.dto.RealEstateResponseDTO;
import rs.securehome.common.dto.RealEstateUpdateDTO;
import rs.securehome.common.dto.RealEstateVisibilityRequestDTO;
import rs.securehome.house.clients.RealEstateClient;

import java.util.Set;

@RestController
@RequestMapping("/api/real-estate")
public class RealEstateController {

    private final RealEstateClient realEstateClient;

    @Autowired
    public RealEstateController(RealEstateClient realEstateClient) {
        this.realEstateClient = realEstateClient;
    }

    @GetMapping("/tenant/{id}")
    public Set<RealEstateResponseDTO> getAllForUser(@RequestHeader("Authorization") String bearerToken, @Nullable @CookieValue("jwt-security") String jwtSecurity, @PathVariable Integer id) {
        return realEstateClient.getAllForTenant(bearerToken, jwtSecurity, id);
    }

    @GetMapping("/{id}")
    public RealEstateResponseDTO getSingleRealEstate(@RequestHeader("Authorization") String bearerToken, @Nullable @CookieValue("jwt-security") String jwtSecurity, @PathVariable Integer id) {
        return realEstateClient.getSingleRealEstate(bearerToken, jwtSecurity, id);
    }

    @PutMapping("/visibility/{type}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setVisibilityForRealEstate(@RequestHeader("Authorization") String bearerToken, @Nullable @CookieValue("jwt-security") String jwtSecurity, @RequestBody RealEstateVisibilityRequestDTO visibilityRequest, @PathVariable String type) {
        realEstateClient.updateVisibility(bearerToken, jwtSecurity, visibilityRequest, type);
    }

    @PostMapping("/create")
    public void createNewRealEstate(@RequestHeader("Authorization") String bearerToken, @Nullable @CookieValue("jwt-security") String jwtSecurity, @RequestBody RealEstateCreateDTO realEstateCreateDTO) {
        realEstateClient.createRealEstate(bearerToken, jwtSecurity, realEstateCreateDTO);
    }

    @PutMapping("/update")
    public void updateNewRealEstate(@RequestHeader("Authorization") String bearerToken, @Nullable @CookieValue("jwt-security") String jwtSecurity, @RequestBody RealEstateUpdateDTO realEstateUpdateDTO) {
        realEstateClient.updateRealEstate(bearerToken, jwtSecurity, realEstateUpdateDTO);
    }

    @PutMapping("/update-name")
    public void updateRealEstateName(@RequestHeader("Authorization") String bearerToken, @Nullable @CookieValue("jwt-security") String jwtSecurity, @RequestBody RealEstateUpdateDTO realEstateUpdateDTO) {
        realEstateClient.updateRealEstateName(bearerToken, jwtSecurity, realEstateUpdateDTO);
    }
}
