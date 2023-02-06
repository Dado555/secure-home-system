package rs.securehome.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.securehome.admin.model.CertificateRequest;


@Repository
public interface CertificateRequestRepository extends JpaRepository<CertificateRequest, Integer> {
}
