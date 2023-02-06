package rs.securehome.house.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import rs.securehome.common.dto.RealEstateCreateDTO;
import rs.securehome.common.dto.RealEstateResponseDTO;
import rs.securehome.common.dto.RealEstateUpdateDTO;
import rs.securehome.common.dto.RealEstateVisibilityRequestDTO;
import rs.securehome.house.config.CustomFeignConfig;

import java.util.Set;

@FeignClient(value = "realEstateClient", url = "https://127.0.0.1:8081/api/real-estate", configuration = CustomFeignConfig.class)
public interface RealEstateClient {

    @GetMapping("/tenant/{id}")
    Set<RealEstateResponseDTO> getAllForTenant(@RequestHeader("Authorization") String bearerToken, @CookieValue("jwt-security") String jwtSecurity, @PathVariable Integer id);

    @GetMapping("/{id}")
    RealEstateResponseDTO getSingleRealEstate(@RequestHeader("Authorization") String bearerToken, @CookieValue("jwt-security") String jwtSecurity, @PathVariable Integer id);

    @PutMapping("/visibility/{type}")
    void updateVisibility(@RequestHeader("Authorization") String bearerToken, @CookieValue("jwt-security") String jwtSecurity, @RequestBody RealEstateVisibilityRequestDTO visibilityRequest, @PathVariable String type);

    @PostMapping("/create")
    void createRealEstate(@RequestHeader("Authorization") String bearerToken, @CookieValue("jwt-security") String jwtSecurity, @RequestBody RealEstateCreateDTO realEstateCreateDTO);

    @PutMapping("/update")
    void updateRealEstate(@RequestHeader("Authorization") String bearerToken, @CookieValue("jwt-security") String jwtSecurity, @RequestBody RealEstateUpdateDTO realEstateUpdateDTO);

    @PutMapping("/update-name")
    void updateRealEstateName(@RequestHeader("Authorization") String bearerToken, @CookieValue("jwt-security") String jwtSecurity, @RequestBody RealEstateUpdateDTO realEstateUpdateDTO);
}
