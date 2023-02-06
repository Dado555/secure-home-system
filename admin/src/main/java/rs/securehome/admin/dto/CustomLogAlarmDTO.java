package rs.securehome.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.securehome.common.enums.LogType;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomLogAlarmDTO {

    private Integer id;

    @NotNull(message = "Log type cannot be null.")
    private LogType type;

    @NotNull(message = "Log substring filter cannot be null.")
    private String substring;
}
