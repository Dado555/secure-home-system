package rs.securehome.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rs.securehome.admin.model.Household;

import java.util.Optional;

@Repository
public interface HouseholdRepository extends JpaRepository<Household, Integer> {

    @Query("select h from User u join u.household h where u.id = :id")
    Optional<Household> getHouseholdForUser(int id);

    @Query("select h from User u join u.household h join fetch h.realEstate where u.id = :id")
    Household getHouseholdForUserWithRealEstate(int id);
}
