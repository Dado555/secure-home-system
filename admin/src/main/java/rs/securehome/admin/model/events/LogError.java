package rs.securehome.admin.model.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kie.api.definition.type.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Role(Role.Type.EVENT)
public class LogError implements AlarmInterface{
    private String ipAddress;
    private String message;
    private String component;
    private String username;
}
