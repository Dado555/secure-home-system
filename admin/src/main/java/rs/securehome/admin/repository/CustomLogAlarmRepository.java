package rs.securehome.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.securehome.admin.model.events.CustomLogAlarm;

@Repository
public interface CustomLogAlarmRepository extends JpaRepository<CustomLogAlarm, Integer> {
}
