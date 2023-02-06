package rs.securehome.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rs.securehome.admin.model.RealEstate;

import java.util.Optional;
import java.util.Set;

public interface RealEstateRepository extends JpaRepository<RealEstate, Integer> {

    @Query("select r from RealEstate r join r.household where r.household.id = :id")
    Set<RealEstate> getAllForHousehold(int id);

    @Query("select r from RealEstate r join r.household where r.household.id = :id")
    Page<RealEstate> getAllForHouseholdPageable(Pageable pageable, int id);

    @Query("select r from RealEstate r join fetch r.household where r.id = :id")
    Optional<RealEstate> getWithHousehold(int id);
}
