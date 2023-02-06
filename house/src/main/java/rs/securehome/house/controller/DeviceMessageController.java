package rs.securehome.house.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import rs.securehome.common.dto.DeviceMessageDTO;
import rs.securehome.common.dto.DeviceMessageFrequencyResponseDTO;
import rs.securehome.common.dto.OwnershipCheckDTO;
import rs.securehome.common.exception.UserNotOwningRealEstateException;
import rs.securehome.house.clients.DeviceClient;
import rs.securehome.house.dto.ReadMessagesRequestDTO;
import rs.securehome.house.model.DeviceMessage;
import rs.securehome.house.service.DeviceMessageService;

import javax.validation.Valid;

@RestController
@RequestMapping("api/messages")
public class DeviceMessageController {

    private final DeviceMessageService deviceMessageService;

    private final DeviceClient deviceClient;

    @Autowired
    public DeviceMessageController(DeviceMessageService deviceMessageService,
                                   DeviceClient deviceClient) {
        this.deviceMessageService = deviceMessageService;
        this.deviceClient = deviceClient;
    }

    @PostMapping("/retrieveFiltered")
    public Page<DeviceMessage> getFilteredMessages(@RequestHeader("Authorization") String bearerToken, @Nullable @CookieValue("jwt-security") String jwtSecurity, @RequestBody @Valid ReadMessagesRequestDTO readMessagesRequestDTO, Pageable pageable) {
        if (!deviceClient.checkForDeviceOwnership(bearerToken, jwtSecurity, new OwnershipCheckDTO(readMessagesRequestDTO.getDeviceIds()))) {
            throw new UserNotOwningRealEstateException("User does not own real estate that contains these devices.");
        }
        return deviceMessageService.getFilteredMessages(readMessagesRequestDTO, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void sendMessage(@RequestBody @Valid DeviceMessageDTO deviceMessageDTO) throws Exception {
        deviceMessageService.archiveMessage(deviceMessageDTO);
    }

    @GetMapping(value = "/frequency")
    public DeviceMessageFrequencyResponseDTO countFrequency(@RequestParam("seconds") Integer seconds, @RequestParam("messagePart") String messagePart) {
        return new DeviceMessageFrequencyResponseDTO(this.deviceMessageService.countFrequency(seconds, messagePart));
    }
}
