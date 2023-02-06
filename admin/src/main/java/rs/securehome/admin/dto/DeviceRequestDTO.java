package rs.securehome.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceRequestDTO {

    @NotBlank(message = "Name must not be blank.")
    private String name;

    @NotBlank(message = "Regex filter must not be blank.")
    private String regexFilter;

    private Integer realEstateId;

    @Override
    public String toString() {
        return "DeviceRequestDTO{" +
                "name='" + name + '\'' +
                ", regexFilter='" + regexFilter + '\'' +
                ", realEstateId=" + realEstateId +
                '}';
    }
}
