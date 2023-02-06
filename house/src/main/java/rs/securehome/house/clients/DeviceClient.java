package rs.securehome.house.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import rs.securehome.common.dto.DeviceResponseDTO;
import rs.securehome.common.dto.HouseholdDevicesDTO;
import rs.securehome.common.dto.OwnershipCheckDTO;
import rs.securehome.house.config.CustomFeignConfig;

import javax.validation.Valid;

@FeignClient(value = "deviceClient", url = "https://127.0.0.1:8081/api/devices", configuration = CustomFeignConfig.class)
public interface DeviceClient {

    @GetMapping("/real-estate/{id}")
    Page<DeviceResponseDTO> getAllDevicesForRealEstate(@RequestHeader("Authorization") String bearerToken, @CookieValue("jwt-security") String jwtSecurity, @SpringQueryMap Pageable pageable, @PathVariable Integer id);

    @GetMapping("/{id}")
    DeviceResponseDTO getSingleDevice(@PathVariable Integer id, @RequestHeader("X-API-Key") String apiKey);

    @PostMapping("/ownership")
    boolean checkForDeviceOwnership(@RequestHeader("Authorization") String bearerToken, @CookieValue("jwt-security") String jwtSecurity, @RequestBody @Valid OwnershipCheckDTO ownershipCheckDTO);

    @GetMapping(value = "/household")
    HouseholdDevicesDTO getAllDevicesForHousehold(@RequestHeader("Authorization") String bearerToken, @CookieValue("jwt-security") String jwtSecurity);
}
