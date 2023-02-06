package rs.securehome.admin.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rs.securehome.admin.model.rules.FrequencyRule;

import java.util.Optional;

@Repository
public interface FrequencyRuleRepository extends MongoRepository<FrequencyRule, String> {

    Optional<FrequencyRule> findByDeviceId(Integer deviceId);
}
