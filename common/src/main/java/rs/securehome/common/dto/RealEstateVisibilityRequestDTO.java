package rs.securehome.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Positive;
import java.util.Arrays;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RealEstateVisibilityRequestDTO {

    private int[] userIds;

    @Positive(message = "Real estate ID must be a positive integer")
    private Integer realEstateId;

    @Override
    public String toString() {
        return "RealEstateVisibilityRequestDTO{" +
                "userIds=" + Arrays.toString(userIds) +
                ", realEstateId=" + realEstateId +
                '}';
    }
}
