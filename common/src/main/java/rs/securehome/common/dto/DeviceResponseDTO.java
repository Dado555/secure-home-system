package rs.securehome.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceResponseDTO {

    private Integer id;

    private String name;

    private String regexFilter;

    @Override
    public String toString() {
        return "DeviceResponseDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", regexFilter='" + regexFilter + '\'' +
                '}';
    }
}
