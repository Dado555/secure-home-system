package rs.securehome.house;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import rs.securehome.common.enums.MessageType;
import rs.securehome.house.model.DeviceMessage;
import rs.securehome.house.repository.DeviceMessageRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class DbInitializer implements ApplicationRunner {

    private final DeviceMessageRepository deviceMessageRepository;

    @Autowired
    public DbInitializer(DeviceMessageRepository deviceMessageRepository) {
        this.deviceMessageRepository = deviceMessageRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        deviceMessageRepository.deleteAll(); // Ovo mora jer nema create-drop lifecycle, bar da ja znam

        deviceMessageRepository.save(new DeviceMessage(UUID.randomUUID().toString(), 1, "Thermometer - ground floor", MessageType.INFO, "The temperature is 21C", LocalDateTime.of(2022, 6, 24, 13, 10)));
        deviceMessageRepository.save(new DeviceMessage(UUID.randomUUID().toString(), 1, "Thermometer - ground floor", MessageType.INFO, "the temperature is 23C", LocalDateTime.of(2022, 6, 24, 13, 12)));
        deviceMessageRepository.save(new DeviceMessage(UUID.randomUUID().toString(), 2, "Thermometer - wine cellar", MessageType.INFO, "The temperature is 22C", LocalDateTime.of(2022, 6, 24, 13, 20)));
        deviceMessageRepository.save(new DeviceMessage(UUID.randomUUID().toString(), 1, "Thermometer - ground floor", MessageType.WARNING, "Low battery", LocalDateTime.of(2022, 6, 24, 13, 11)));
        deviceMessageRepository.save(new DeviceMessage(UUID.randomUUID().toString(), 1, "Thermometer - ground floor", MessageType.ERROR, "Shutting down", LocalDateTime.of(2022, 6, 24, 13, 15)));
    }
}
