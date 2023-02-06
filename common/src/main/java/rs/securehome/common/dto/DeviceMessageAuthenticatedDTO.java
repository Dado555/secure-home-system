package rs.securehome.common.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceMessageAuthenticatedDTO {

    private DeviceMessageDTO deviceMessageDTO;

    private String apiKey;
}
