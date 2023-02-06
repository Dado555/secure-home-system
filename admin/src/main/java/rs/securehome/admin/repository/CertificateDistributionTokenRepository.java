package rs.securehome.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.securehome.admin.model.CertificateDistributionToken;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CertificateDistributionTokenRepository extends JpaRepository<CertificateDistributionToken, Integer> {

    CertificateDistributionToken getCertificateDistributionTokenByAlias(String alias);

    Optional<CertificateDistributionToken> getCertificateDistributionTokenByValue(UUID value);

}
