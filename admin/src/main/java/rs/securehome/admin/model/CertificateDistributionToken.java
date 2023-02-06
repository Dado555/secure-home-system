package rs.securehome.admin.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "certificate_distribution_tokens")
@Getter
@Setter
public class CertificateDistributionToken {

    public static final int EXPIRATION_DAYS = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "value", nullable = false)
    private UUID value;

    @Column(name = "alias")
    private String alias;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "used_for_certificate")
    private Boolean usedForCertificate;

    @Column(name = "used_for_key")
    private Boolean usedForKey;

    public CertificateDistributionToken() {

    }

    public CertificateDistributionToken(String alias) {
        this.alias = alias;
        this.createdAt = LocalDateTime.now();
        this.value = UUID.randomUUID();
        this.usedForKey = false;
        this.usedForCertificate = false;
    }

    public CertificateDistributionToken(UUID value, String alias, LocalDateTime createdAt) {
        this.value = value;
        this.alias = alias;
        this.createdAt = createdAt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.createdAt.plusDays(EXPIRATION_DAYS));
    }

}
