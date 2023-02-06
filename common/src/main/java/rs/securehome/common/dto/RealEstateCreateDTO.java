package rs.securehome.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RealEstateCreateDTO {
    private String name;
    private int[] canBeSeenByIds;
    private Integer householdId;
    private Integer readingPeriod;

    @Override
    public String toString() {
        return "RealEstateCreateDTO{" +
                "name='" + name + '\'' +
                ", canBeSeenByIds=" + Arrays.toString(canBeSeenByIds) +
                ", householdId=" + householdId +
                ", readingPeriod=" + readingPeriod +
                '}';
    }
}
