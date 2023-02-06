package rs.securehome.admin.model;

import lombok.Getter;
import lombok.Setter;
import rs.securehome.admin.model.enums.ExtendedKeyUsage;
import rs.securehome.admin.model.enums.KeyUsage;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "certificate_requests")
public class CertificateRequest extends BaseEntity {

    @Column(name = "validity_start", nullable = false)
    private Date validityStart;

    @Column(name = "validity_end", nullable = false)
    private Date validityEnd;

    @Column(name = "authority_key_identifier", nullable = false)
    private Boolean authorityKeyIdentifier;

    @Column(name = "subject_key_identifier", nullable = false)
    private Boolean subjectKeyIdentifier;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> subjectAlternativeNames;

    @Column(name = "common_name", nullable = false)
    private String commonName;

    @Column
    @Enumerated
    @ElementCollection(targetClass = KeyUsage.class, fetch = FetchType.EAGER)
    private Set<KeyUsage> keyUsages;

    @Column
    @Enumerated
    @ElementCollection(targetClass = ExtendedKeyUsage.class, fetch = FetchType.EAGER)
    private Set<ExtendedKeyUsage> extendedKeyUsages;

    public CertificateRequest() {}

}
