package rs.securehome.house.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import rs.securehome.common.dto.OwnershipCheckDTO;
import rs.securehome.common.dto.ReportRequestDTO;
import rs.securehome.common.exception.UserNotOwningRealEstateException;
import rs.securehome.house.clients.DeviceClient;
import rs.securehome.house.dto.ReportResponseDTO;
import rs.securehome.house.service.DeviceMessageService;

@RestController
@RequestMapping("api/reports")
public class ReportController {

    private final DeviceMessageService deviceMessageService;

    private final DeviceClient deviceClient;

    @Autowired
    public ReportController(DeviceMessageService deviceMessageService, DeviceClient deviceClient) {
        this.deviceMessageService = deviceMessageService;
        this.deviceClient = deviceClient;
    }

    @PostMapping
    public ReportResponseDTO getReport(@RequestHeader("Authorization") String bearerToken, @Nullable @CookieValue("jwt-security") String jwtSecurity, @RequestBody ReportRequestDTO reportRequestDTO) {
        if(!deviceClient.checkForDeviceOwnership(bearerToken, jwtSecurity, new OwnershipCheckDTO(reportRequestDTO.getDeviceIds()))){
            throw new UserNotOwningRealEstateException("User does not own real estate that contains these devices.");
        }
        return deviceMessageService.getMessageReport(reportRequestDTO);
    }
}
