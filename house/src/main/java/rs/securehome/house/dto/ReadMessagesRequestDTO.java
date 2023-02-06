package rs.securehome.house.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.securehome.common.enums.MessageType;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReadMessagesRequestDTO {

    @NotNull(message = "You need to specify which device messages are you going to read")
    private int[] deviceIds;

    private String deviceName;

    private String message;

    private MessageType messageType;

    private LocalDateTime from;

    private LocalDateTime to;
}
