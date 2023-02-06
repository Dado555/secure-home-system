package rs.securehome.admin.model.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.securehome.common.enums.LogType;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "custom_log_alarm")
public class CustomLogAlarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "type", nullable = false)
    private LogType type;

    @Column(name = "substring", nullable = false)
    private String substring;

    public CustomLogAlarm(LogType type, String substring) {
        this.type = type;
        this.substring = substring;
    }
}
