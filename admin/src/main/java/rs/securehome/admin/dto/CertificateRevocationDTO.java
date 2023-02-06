package rs.securehome.admin.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.securehome.admin.model.enums.CertificateRevocationReason;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificateRevocationDTO {

    @NotBlank(message = "Certificate alias cannot be blank.")
    private String certificateAlias;

    @NotNull(message = "Certificate revocation reason cannot be null.")
    private CertificateRevocationReason reason;


    @Override
    public String toString() {
        return "CertificateRevocationDTO{" +
                "certificateAlias='" + certificateAlias + '\'' +
                ", reason=" + reason +
                '}';
    }
}
