package rs.securehome.house.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponseDTO {

    private Integer numberOfMessages;

    private Integer numberOfInformations;

    private Integer numberOfWarnings;

    private Integer numberOfErrors;

    private Integer numberOfDeviceAlarms;
}
