package rs.securehome.house.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import rs.securehome.common.enums.MessageType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document("device_messages")
public class DeviceMessage {

    @Id
    private String id;

    private Integer deviceId;

    private String deviceName;

    private MessageType messageType;

    private String message;

    private LocalDateTime timestamp;
}
