package rs.securehome.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import rs.securehome.admin.exception.ApiKeyException;
import rs.securehome.admin.exception.InvalidRegularExpressionException;
import rs.securehome.admin.exception.NotFoundException;
import rs.securehome.admin.model.Device;
import rs.securehome.admin.model.User;
import rs.securehome.admin.repository.DeviceRepository;
import rs.securehome.admin.repository.HouseholdRepository;
import rs.securehome.admin.repository.RealEstateRepository;
import rs.securehome.admin.repository.UserRepository;
import rs.securehome.common.exception.UserNotOwningRealEstateException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final RealEstateRepository realEstateRepository;
    private final HouseholdRepository householdRepository;
    private final UserRepository userRepository;

    @Value("${API_KEY}")
    private String apiKey;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository,
                         RealEstateRepository realEstateRepository,
                         HouseholdRepository householdRepository, UserRepository userRepository) {
        this.deviceRepository = deviceRepository;
        this.realEstateRepository = realEstateRepository;
        this.householdRepository = householdRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public boolean checkDeviceOwnership(int[] deviceIds) {
        var currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        for (int deviceId : deviceIds) {
            var device = deviceRepository.findById(deviceId);
            if (device.isEmpty()) {
                return false;
            }

            var ownersList = device.get().getRealEstate().getCanBeSeenBy();
            if (ownersList.stream().noneMatch((user) -> user.getId().equals(currentUser.getId()))) {
                return false;
            }
        }
        return true;
    }

    public Page<Device> getAllDevicesForRealEstate(Pageable pageable, Integer realEstateId) {
        var currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!currentUser.getAuthority().getName().equals("ADMIN") && householdRepository.getHouseholdForUserWithRealEstate(currentUser.getId()).getRealEstate().stream().noneMatch((re) -> re.getId().equals(realEstateId))) {
            throw new UserNotOwningRealEstateException("User with id: " + currentUser.getId() + " does not own that real estate.");
        }

        return deviceRepository.getAllDevicesForRealEstate(pageable, realEstateId);
    }

    public Device getSingleDevice(Integer id, String aKey) {
        if (!aKey.equals(apiKey)) {
            throw new ApiKeyException("Invalid API key.");
        }
        return deviceRepository.findById(id).orElseThrow(() -> new NotFoundException("Device with id: " + id + " does not exist"));
    }

    public Device createDeviceForRealEstate(Device device, Integer realEstateId) {
        try {
            Pattern.compile(device.getRegexFilter());
        } catch (PatternSyntaxException exception) {
            if (!device.getRegexFilter().equals("*")) {
                throw new InvalidRegularExpressionException("Filter is not a valid regular expression.");
            }
        }

        var realEstate = realEstateRepository.findById(realEstateId).orElseThrow(() -> new NotFoundException("Real estate with id: " + realEstateId + " does not exist."));
        device.setRealEstate(realEstate);
        var newDevice = deviceRepository.save(device);

        realEstate.addDevice(device);
        realEstateRepository.save(realEstate);

        return newDevice;
    }

    public void updateDevice(Device device, Integer id) {
        var deviceToUpdate = deviceRepository.findById(id).orElseThrow(() -> new NotFoundException("Device with id: " + id + " does not exist."));
        deviceToUpdate.setName(device.getName());

        try {
            Pattern.compile(device.getRegexFilter());
        } catch (PatternSyntaxException exception) {
            if (!device.getRegexFilter().equals("*")) {
                throw new InvalidRegularExpressionException("Filter is not a valid regular expression.");
            }
        }

        deviceToUpdate.setRegexFilter(device.getRegexFilter());
        deviceRepository.save(deviceToUpdate);
    }

    public void deleteDevice(Integer id) {
        var deviceToDelete = deviceRepository.findById(id).orElseThrow(() -> new NotFoundException("Device with id: " + id + " does not exist."));
        deviceRepository.delete(deviceToDelete);
    }

    public List<Device> getDevicesForHousehold() {
        //id korisnika se dobavlja iz konteksta, pa se na osnovu toga dobavlja household i njegovi uredjaji

        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = this.userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException(String.format("User with username: %s does not exist.", username)));

        var userId = user.getId();
        var household = this.householdRepository.getHouseholdForUserWithRealEstate(userId);

        var devices = new ArrayList<Device>();

        household.getRealEstate().stream()
                .filter(realEstate -> realEstate.getCanBeSeenBy().contains(user))
                .forEach(realEstate -> devices.addAll(realEstate.getDevices()));

        return devices;

    }
}
