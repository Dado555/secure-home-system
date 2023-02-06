package rs.securehome.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rs.securehome.admin.model.Authority;


@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Integer> {

    @Query("select a from Authority a where a.name = :name")
    Authority findByName(String name);
}
