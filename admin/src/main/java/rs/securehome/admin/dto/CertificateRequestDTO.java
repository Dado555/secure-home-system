package rs.securehome.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.securehome.admin.model.enums.ExtendedKeyUsage;
import rs.securehome.admin.model.enums.KeyUsage;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificateRequestDTO {

    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "You have to insert a name")
    private String name;

    @NotBlank(message = "You have to insert a surname")
    private String surname;

    @NotBlank(message = "You have to insert an organization")
    private String organization;

    @NotBlank(message = "You have to insert an organization unit")
    private String organizationUnit;

    @NotBlank(message = "You have to insert a common name")
    private String commonName;

    @NotBlank(message = "You have to insert a city")
    private String city;

    @NotBlank(message = "You have to insert a provicne")
    private String province;

    @NotBlank(message = "You have to insert a country code")
    private String countryCode;

    @NotNull(message = "You have to insert a start date")
    private Date validityStart;

    @NotNull(message = "You have to insert an end date")
    private Date validityEnd;

    private Boolean authorityKeyIdentifier;

    private Boolean subjectKeyIdentifier;

    private List<String> subjectAlternativeNames;

    private List<KeyUsage> keyUsages;

    private List<ExtendedKeyUsage> extendedKeyUsages;


    @Override
    public String toString() {
        return "CertificateRequestDTO{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", organization='" + organization + '\'' +
                ", organizationUnit='" + organizationUnit + '\'' +
                ", commonName='" + commonName + '\'' +
                ", city='" + city + '\'' +
                ", province='" + province + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", validityStart=" + validityStart +
                ", validityEnd=" + validityEnd +
                ", authorityKeyIdentifier=" + authorityKeyIdentifier +
                ", subjectKeyIdentifier=" + subjectKeyIdentifier +
                ", subjectAlternativeNames=" + subjectAlternativeNames +
                ", keyUsages=" + keyUsages +
                ", extendedKeyUsages=" + extendedKeyUsages +
                '}';
    }
}
