package rs.securehome.admin.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TemperatureRuleDTO {


    @NotNull(message = "Device id cannot be null.")
    private Integer deviceId;

    @NotNull(message = "Min temperature cannot be null.")
    private Double minTemperature;

    @NotNull(message = "Max temperature cannot be null.")
    private Double maxTemperature;

    @NotBlank(message = "Temperature scale cannot be blank.")
    private String temperatureScale;

}
