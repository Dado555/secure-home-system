package rs.securehome.admin.model.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("alarm")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Alarm {

    @Id
    private String id;

    private AlarmType type;
    private AlarmInterface alarm;
    private LocalDateTime timestamp;

    public Alarm(AlarmType type, AlarmInterface alarm, LocalDateTime timestamp) {
        this.type = type;
        this.alarm = alarm;
        this.timestamp = timestamp;
    }
}
