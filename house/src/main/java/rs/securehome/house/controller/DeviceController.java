package rs.securehome.house.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import rs.securehome.common.dto.DeviceResponseDTO;
import rs.securehome.house.clients.DeviceClient;

@RestController
@RequestMapping("api/devices")
public class DeviceController {

    private final DeviceClient deviceClient;

    @Autowired
    public DeviceController(DeviceClient deviceClient) {
        this.deviceClient = deviceClient;
    }

    @GetMapping("/real-estate/{id}")
    Page<DeviceResponseDTO> getAllDevicesForRealEstate(@RequestHeader("Authorization") String bearerToken, @Nullable @CookieValue("jwt-security") String jwtSecurity, @PathVariable Integer id, Pageable pageable) {
        return deviceClient.getAllDevicesForRealEstate(bearerToken, jwtSecurity, pageable, id);
    }
}
