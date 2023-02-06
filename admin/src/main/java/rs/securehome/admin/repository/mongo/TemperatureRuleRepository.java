package rs.securehome.admin.repository.mongo;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import rs.securehome.admin.model.rules.TemperatureRule;

import java.util.Optional;

@Repository
public interface TemperatureRuleRepository extends MongoRepository<TemperatureRule, String> {

    @Query("{deviceId: ?0}")
    Optional<TemperatureRule> findByDeviceId(Integer deviceId);
}
