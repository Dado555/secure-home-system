package rs.securehome.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rs.securehome.admin.model.events.Alarm;
import rs.securehome.admin.model.events.AlarmType;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface AlarmRepository extends MongoRepository<Alarm, String> {
    Alarm findById(Integer id);

    List<Alarm> findAllByType(AlarmType type);

    Page<Alarm> findAllByIdNotNull(Pageable pageable);

    Page<Alarm> findAllByTypeInAndTimestampBetween(Collection<AlarmType> type,
                                                                               LocalDateTime timestampFrom,
                                                                               LocalDateTime timestampTo,
                                                                               Pageable pageable);
}
