package rs.securehome.house.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Alarm {

    @Id
    private String id;

    private Integer deviceId;

    private String deviceMessage;

    private String deviceName;

    private LocalDateTime timestamp;

    public Alarm(Integer deviceId, String deviceMessage, String deviceName) {
        this.deviceId = deviceId;
        this.deviceMessage = deviceMessage;
        this.deviceName = deviceName;
        this.timestamp = LocalDateTime.now();
    }
}
