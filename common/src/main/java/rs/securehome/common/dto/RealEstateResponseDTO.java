package rs.securehome.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RealEstateResponseDTO {

    private Integer id;

    private String name;

    private Set<UserResponseDTO> canBeSeenBy;

    private Integer deviceReadingPeriod;

    @Override
    public String toString() {
        return "RealEstateResponseDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", canBeSeenBy=" + canBeSeenBy +
                ", readingPeriod=" + deviceReadingPeriod +
                '}';
    }
}
