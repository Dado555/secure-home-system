package rs.securehome.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.securehome.admin.model.BlacklistedJWT;

import java.util.Optional;

@Repository
public interface BlacklistedJWTRepository extends JpaRepository<BlacklistedJWT, Integer> {

    Optional<BlacklistedJWT> findBlacklistedJWTByJwt(String jwt);
}
