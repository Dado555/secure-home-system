package rs.securehome.admin.model.rules;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "frequency_rules")
@NoArgsConstructor
public class FrequencyRule {

    @Id
    private String id;

    private Integer deviceId;

    private Integer numberOfOccurrences;

    private Integer seconds;


    public FrequencyRule(Integer deviceId, Integer numberOfOccurrences, Integer seconds) {
        this.deviceId = deviceId;
        this.numberOfOccurrences = numberOfOccurrences;
        this.seconds = seconds;
    }
}
