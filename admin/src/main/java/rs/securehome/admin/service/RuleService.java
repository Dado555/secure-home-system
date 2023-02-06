package rs.securehome.admin.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rs.securehome.admin.clients.DeviceMessageClient;
import rs.securehome.admin.dto.FrequencyRuleDTO;
import rs.securehome.admin.dto.TemperatureRuleDTO;
import rs.securehome.admin.exception.ApiKeyException;
import rs.securehome.admin.exception.NotFoundException;
import rs.securehome.admin.exception.RuleException;
import rs.securehome.admin.model.rules.FrequencyRule;
import rs.securehome.admin.model.rules.TemperatureRule;
import rs.securehome.admin.repository.DeviceRepository;
import rs.securehome.admin.repository.mongo.FrequencyRuleRepository;
import rs.securehome.admin.repository.mongo.TemperatureRuleRepository;
import rs.securehome.common.dto.DeviceMessageAuthenticatedDTO;
import rs.securehome.common.dto.DeviceMessageDTO;

import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public class RuleService {


    private final TemperatureRuleRepository temperatureRuleRepository;

    private final FrequencyRuleRepository frequencyRuleRepository;

    private final DeviceMessageClient deviceMessageClient;

    private final DeviceRepository deviceRepository;


    @Value("${API_KEY}")
    private String apiKey;

    @Autowired
    public RuleService(TemperatureRuleRepository temperatureRuleRepository, FrequencyRuleRepository frequencyRuleRepository, DeviceMessageClient deviceMessageClient, DeviceRepository deviceRepository) {
        this.temperatureRuleRepository = temperatureRuleRepository;
        this.frequencyRuleRepository = frequencyRuleRepository;
        this.deviceMessageClient = deviceMessageClient;
        this.deviceRepository = deviceRepository;
    }

    public List<TemperatureRule> getAllTemperatureRules() {
        return this.temperatureRuleRepository.findAll();
    }

    public List<FrequencyRule> getAllFrequencyRules() {
        return this.frequencyRuleRepository.findAll();
    }


    private boolean alarmShouldBeFiredForTemperatureRule(DeviceMessageDTO deviceMessageDTO) {
        var tokens = deviceMessageDTO.getMessage().split(":");
        var tempAndScale = tokens[1].strip().substring(0, tokens[1].strip().length() - 1);
        var temp = Double.parseDouble(tempAndScale.substring(0, tempAndScale.length() - 1));
        var scale = tempAndScale.charAt(tempAndScale.length() - 1) + "";

        var optionalRuleForDevice = this.temperatureRuleRepository.findByDeviceId(deviceMessageDTO.getDeviceID());
        if (optionalRuleForDevice.isEmpty()) {
            return false;
        } else {
            var ruleForDevice = optionalRuleForDevice.get();
            if (ruleForDevice.getTemperatureScale().equals(scale)) {
                var minTemp = ruleForDevice.getMinTemperature();
                var maxTemp = ruleForDevice.getMaxTemperature();
                return temp <= minTemp || temp >= maxTemp;
            } else
                return false;
        }
    }

    private boolean alarmShouldBeFiredForFrequencyRule(DeviceMessageDTO deviceMessageDTO, String messagePart) {
        var optionalFrequencyRule = this.frequencyRuleRepository.findByDeviceId(deviceMessageDTO.getDeviceID());
        if (optionalFrequencyRule.isEmpty())
            return false;
        else {
            var rule = optionalFrequencyRule.get();
            var response = this.deviceMessageClient.countFrequency(rule.getSeconds(), messagePart);
            return response.getFrequency() >= rule.getNumberOfOccurrences();
        }
    }


    public boolean alarmShouldBeFired(DeviceMessageAuthenticatedDTO deviceMessageAuthenticatedDTO) {
        if (!deviceMessageAuthenticatedDTO.getApiKey().equals(apiKey))
            throw new ApiKeyException("Api key not valid.");
        var deviceMessageDTO = deviceMessageAuthenticatedDTO.getDeviceMessageDTO();
        var message = deviceMessageDTO.getMessage().toLowerCase(Locale.ROOT);
        if (message.contains("temperature"))
            return this.alarmShouldBeFiredForTemperatureRule(deviceMessageDTO);
        else if (message.contains("door"))
            return this.alarmShouldBeFiredForFrequencyRule(deviceMessageDTO, "opened");
        else if (message.contains("smoke detected"))
            return true;
        else if (message.contains("motion"))
            return this.alarmShouldBeFiredForFrequencyRule(deviceMessageDTO, "Motion");
        return false;
    }

    private void checkForDevice(Integer deviceId) {
        this.deviceRepository.findById(deviceId)
                .orElseThrow(() -> new NotFoundException(String.format("Device with id: %d does not exist.", deviceId)));
    }

    public TemperatureRule createTemperatureRule(TemperatureRuleDTO dto) {
        var deviceId = dto.getDeviceId();

        this.checkForDevice(deviceId);

        var minTemp = dto.getMinTemperature();
        var maxTemp = dto.getMaxTemperature();
        var scale = dto.getTemperatureScale();
        if (!scale.equals("C") && !scale.equals("F"))
            throw new RuleException(String.format("Temperature scale can be either C or F. Found: %s.", scale));

        var ruleForDevice = this.temperatureRuleRepository.findByDeviceId(dto.getDeviceId());
        if (ruleForDevice.isPresent())
            throw new RuleException(String.format("Rule for device with id: %d already exists.", deviceId));
        if (minTemp >= maxTemp)
            throw new RuleException("Min temperature cannot be higher or equal to max temperature in temperature rule.");
        return this.temperatureRuleRepository.save(new TemperatureRule(deviceId, minTemp, maxTemp, scale));
    }

    public FrequencyRule createFrequencyRule(FrequencyRuleDTO frequencyRuleDTO) {
        var deviceId = frequencyRuleDTO.getDeviceId();
        this.checkForDevice(deviceId);
        var ruleForDevice = this.frequencyRuleRepository.findByDeviceId(deviceId);
        if (ruleForDevice.isPresent())
            throw new RuleException(String.format("Rule for device with id: %d already exists.", deviceId));
        return this.frequencyRuleRepository.save(new FrequencyRule(deviceId, frequencyRuleDTO.getNumberOfOccurrences(), frequencyRuleDTO.getSeconds()));
    }
}
