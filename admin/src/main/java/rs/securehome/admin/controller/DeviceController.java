package rs.securehome.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import rs.securehome.admin.annotation.Log;
import rs.securehome.admin.dto.DeviceRequestDTO;
import rs.securehome.admin.model.Device;
import rs.securehome.admin.service.DeviceService;
import rs.securehome.common.dto.DeviceResponseDTO;
import rs.securehome.common.dto.HouseholdDevicesDTO;
import rs.securehome.common.dto.OwnershipCheckDTO;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("api/devices")
public class DeviceController {

    private final DeviceService deviceService;

    @Autowired
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Log(message = "Fetching all devices for a real estate with a given id.")
    @GetMapping("/real-estate/{id}")
    public Page<DeviceResponseDTO> getDevicesForRealEstate(HttpServletRequest request, @PathVariable Integer id, Pageable pageable) {
        return deviceService.getAllDevicesForRealEstate(pageable, id).map(d -> new DeviceResponseDTO(d.getId(), d.getName(), d.getRegexFilter()));
    }

    @Log(message = "Reading configuration for device with a given id.")
    @GetMapping("/{id}")
    public DeviceResponseDTO getSingleDevice(HttpServletRequest request, @PathVariable Integer id, @RequestHeader("X-API-Key") String apiKey) {
        var device = deviceService.getSingleDevice(id, apiKey);
        return new DeviceResponseDTO(device.getId(), device.getName(), device.getRegexFilter());
    }

    @Log(message = "Creating a new device.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeviceResponseDTO createDevice(HttpServletRequest request, @RequestBody @Valid DeviceRequestDTO deviceRequestDTO) {
        var newDevice = deviceService.createDeviceForRealEstate(new Device(deviceRequestDTO.getName(), deviceRequestDTO.getRegexFilter(), null), deviceRequestDTO.getRealEstateId());
        return new DeviceResponseDTO(newDevice.getId(), newDevice.getName(), newDevice.getRegexFilter());
    }

    @Log(message = "Checking device(s) ownership from id(s).")
    @PostMapping("/ownership")
    @ResponseStatus(HttpStatus.OK)
    public boolean checkForDeviceOwnership(HttpServletRequest request, @RequestBody @Valid OwnershipCheckDTO ownershipCheckDTO) {
        return deviceService.checkDeviceOwnership(ownershipCheckDTO.getDeviceIds());
    }

    @Log(message = "Updating a device with a given id.")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateDevice(HttpServletRequest request, @RequestBody @Valid DeviceRequestDTO deviceRequestDTO, @PathVariable Integer id) {
        deviceService.updateDevice(new Device(deviceRequestDTO.getName(), deviceRequestDTO.getRegexFilter(), null), id);
    }

    @Log(message = "Deleting a device with a given id.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDevice(HttpServletRequest request, @PathVariable Integer id) {
        deviceService.deleteDevice(id);
    }


    @Log(message = "Fetching device ids for the household of the currently logged user.")
    @GetMapping(value = "/household")
    public HouseholdDevicesDTO getAllDevicesForHousehold(HttpServletRequest request) {
        var deviceIds = this.deviceService.getDevicesForHousehold()
                .stream().map(Device::getId).toArray(Integer[]::new);
        return new HouseholdDevicesDTO(deviceIds);
    }

}
