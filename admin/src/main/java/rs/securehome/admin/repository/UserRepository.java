package rs.securehome.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rs.securehome.admin.model.User;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    @Query(value = "select u from User u join u.household h where h.id = :id")
    Page<User> getAllUsersForHousehold(Pageable pageable, int id);

    List<User> getAllByHouseholdId(int id);

    @Query("select h.tenants from User u join u.household h where u.id = :id")
    List<User> getTenantsForHouseHead(int id);
}
