package rs.securehome.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.securehome.common.enums.MessageType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceMessageDTO {

    @Positive(message = "Device ID must be a positive number")
    private Integer deviceID;

    @NotBlank(message = "Message cannot be blank")
    private String message;

    @NotNull(message = "Message type cannot be null")
    private MessageType messageType;

    @NotNull(message = "You have to provide a timestamp")
    private LocalDateTime timestamp;
}
