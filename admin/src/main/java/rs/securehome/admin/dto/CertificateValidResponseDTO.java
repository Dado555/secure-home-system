package rs.securehome.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificateValidResponseDTO {
    private boolean certificateValid;

    private String message;


    public CertificateValidResponseDTO(boolean certificateValid) {
        this.certificateValid = certificateValid;
        this.message = "";
    }

    @Override
    public String toString() {
        return "CertificateValidResponseDTO{" +
                "certificateValid=" + certificateValid +
                ", message='" + message + '\'' +
                '}';
    }
}
