package rs.securehome.house.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import rs.securehome.common.dto.DeviceMessageAuthenticatedDTO;
import rs.securehome.common.dto.DeviceMessageDTO;
import rs.securehome.common.dto.ReportRequestDTO;
import rs.securehome.common.enums.MessageType;
import rs.securehome.house.clients.DeviceClient;
import rs.securehome.house.clients.RuleClient;
import rs.securehome.house.dto.ReadMessagesRequestDTO;
import rs.securehome.house.dto.ReportResponseDTO;
import rs.securehome.house.exception.MessageFromUnknownSourceException;
import rs.securehome.house.model.Alarm;
import rs.securehome.house.model.DeviceMessage;
import rs.securehome.house.repository.AlarmRepository;
import rs.securehome.house.repository.DeviceMessageRepository;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Service
@Slf4j
public class DeviceMessageService {

    private final DeviceMessageRepository deviceMessageRepository;

    private final AlarmRepository alarmRepository;

    private final DeviceClient deviceClient;

    private final RuleClient ruleClient;

    private final SecretKey secretKey;

    private static final IvParameterSpec IV_PARAMETER_SPEC = new IvParameterSpec(new byte[]{15, 93, 67, 92, 93, 64, 28, 80, 6, 88, 38, 98, 26, 12, 15, 59});


    @Value("${API_KEY}")
    private String apiKey;

    @Autowired
    public DeviceMessageService(DeviceMessageRepository deviceMessageRepository,
                                AlarmRepository alarmRepository, DeviceClient deviceClient,
                                RuleClient ruleClient, SecretKey secretKey) {
        this.deviceMessageRepository = deviceMessageRepository;
        this.alarmRepository = alarmRepository;
        this.deviceClient = deviceClient;
        this.ruleClient = ruleClient;
        this.secretKey = secretKey;
    }

    public Page<DeviceMessage> getFilteredMessages(ReadMessagesRequestDTO params, Pageable pageable) {
        return deviceMessageRepository.getFilteredMessagesForDeviceList(params.getDeviceIds(), params.getDeviceName(), params.getMessageType(), params.getMessage(), params.getFrom(), params.getTo(), pageable);
    }

    public ReportResponseDTO getMessageReport(ReportRequestDTO reportRequest) {
        var infoMessagesCount = deviceMessageRepository.getMessgeCount(reportRequest.getDeviceIds(), MessageType.INFO, reportRequest.getFrom(), reportRequest.getTo());
        var warningMessagesCount = deviceMessageRepository.getMessgeCount(reportRequest.getDeviceIds(), MessageType.WARNING, reportRequest.getFrom(), reportRequest.getTo());
        var errorMessagesCount = deviceMessageRepository.getMessgeCount(reportRequest.getDeviceIds(), MessageType.ERROR, reportRequest.getFrom(), reportRequest.getTo());

        var deviceAlarmCount = alarmRepository.getAlarmCountById(reportRequest.getDeviceIds(), reportRequest.getFrom(), reportRequest.getTo());

        return new ReportResponseDTO(infoMessagesCount + warningMessagesCount + errorMessagesCount, infoMessagesCount, warningMessagesCount, errorMessagesCount, deviceAlarmCount);
    }

    private String decryptMessage(String encryptedMessage) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException {
        Cipher aesCipherDec = Cipher.getInstance("AES/CBC/PKCS5Padding");
        aesCipherDec.init(Cipher.DECRYPT_MODE, secretKey, IV_PARAMETER_SPEC);
        Cipher aesCipherEnc = Cipher.getInstance("AES/CBC/PKCS5Padding");
        aesCipherEnc.init(Cipher.ENCRYPT_MODE, secretKey, IV_PARAMETER_SPEC);

        var encryptedMessageBytes = Base64Utils.decodeFromString(encryptedMessage);
        var decryptedMessage = new String(aesCipherDec.doFinal(encryptedMessageBytes));

        if (!Arrays.equals(aesCipherEnc.doFinal(decryptedMessage.getBytes()), encryptedMessageBytes)) {
            throw new MessageFromUnknownSourceException("Message arrived from unknown source.");
        }
        return decryptedMessage;
    }


    public void archiveMessage(DeviceMessageDTO deviceMessageDTO) throws Exception {

        var decryptedMessage = this.decryptMessage(deviceMessageDTO.getMessage());
        var device = deviceClient.getSingleDevice(deviceMessageDTO.getDeviceID(), apiKey);
        deviceMessageDTO.setMessage(decryptedMessage);
        if (this.ruleClient.alarmShouldBeFired(new DeviceMessageAuthenticatedDTO(deviceMessageDTO, apiKey))) {
            log.info("Opalio alarm. Poruka: " + decryptedMessage);
            this.alarmRepository.save(new Alarm(deviceMessageDTO.getDeviceID(), decryptedMessage, device.getName()));
        }

        // Samo INFO poruke se filtriraju, greske i upozorenja nema svrhe izbjegavati
        if (!device.getRegexFilter().equals("*") && deviceMessageDTO.getMessageType().equals(MessageType.INFO) && !decryptedMessage.matches(device.getRegexFilter())) {
            return;
        }

        var newMessage = new DeviceMessage(UUID.randomUUID().toString(), device.getId(), device.getName(), deviceMessageDTO.getMessageType(), decryptedMessage, deviceMessageDTO.getTimestamp());
        deviceMessageRepository.save(newMessage);
    }


    public Integer countFrequency(Integer seconds, String messagePart) {
        var now = LocalDateTime.now();
        var startDate = now.minusSeconds(seconds);
        return this.deviceMessageRepository.countFrequency(startDate, now, messagePart);
    }
}
