package rs.securehome.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificateResponseDTO {
    private String alias;
    private String certificate;
    private String aki;

    @Override
    public String toString() {
        return "CertificateResponseDTO{" +
                "alias='" + alias + '\'' +
                ", certificate='" + certificate + '\'' +
                ", aki='" + aki + '\'' +
                '}';
    }
}
