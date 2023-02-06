package rs.securehome.admin.model.rules;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "temperature_rules")
public class TemperatureRule {

    @Id
    private String id;

    private Integer deviceId;

    private Double minTemperature;

    private Double maxTemperature;

    private String temperatureScale;


    public TemperatureRule() {
    }

    public TemperatureRule(Integer deviceId, Double minTemperature, Double maxTemperature, String temperatureScale) {
        this.deviceId = deviceId;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.temperatureScale = temperatureScale;
    }
}
