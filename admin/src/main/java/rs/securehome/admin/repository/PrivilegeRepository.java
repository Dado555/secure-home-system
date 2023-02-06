package rs.securehome.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.securehome.admin.model.Privilege;

public interface PrivilegeRepository extends JpaRepository<Privilege, Integer> {
}
