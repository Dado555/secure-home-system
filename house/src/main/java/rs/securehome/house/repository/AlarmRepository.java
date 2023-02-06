package rs.securehome.house.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import rs.securehome.house.model.Alarm;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlarmRepository extends MongoRepository<Alarm, String> {

    @Query("{deviceId: {" +
            "   $in: ?0" +
            "   }" +
            "}")
    List<Alarm> getAlarmsByDevicesIds(Integer[] deviceIds, Sort sort);

    @Query(value = "{deviceId: { $in: ?0 }, timestamp: {$gte: ?1, $lte: ?2} }", count = true)
    int getAlarmCountById(int[] deviceIds, LocalDateTime from, LocalDateTime to);
}
