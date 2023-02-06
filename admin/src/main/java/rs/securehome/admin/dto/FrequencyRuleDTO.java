package rs.securehome.admin.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FrequencyRuleDTO {

    @NotNull(message = "Device id cannot be null.")
    private Integer deviceId;

    @NotNull(message = "Number of occurrences cannot be null.")
    @Min(value = 1, message = "Number of occurrences can be 1 at the lowest.")
    private Integer numberOfOccurrences;

    @NotNull(message = "Number of seconds cannot be null.")
    @Min(value = 2, message = "Number of seconds can be 2 at the lowest.")
    private Integer seconds;
}
