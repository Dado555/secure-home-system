package rs.securehome.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.securehome.admin.model.FailedLoginAttempt;

import java.time.LocalDateTime;

@Repository
public interface FailedLoginAttemptRepository extends JpaRepository<FailedLoginAttempt, Integer> {


    @Query(value = "select count(fla) from FailedLoginAttempt fla where fla.user.id = :userId and fla.attemptTime >= :fromTime and fla.attemptTime <= :toTime")
    int countAttemptsInPeriodForUser(@Param("userId") Integer userId, @Param("fromTime") LocalDateTime fromTime, @Param("toTime") LocalDateTime toTime);
}
