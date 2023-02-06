package rs.securehome.common.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HouseholdResponseDTO {

    private int id;

    private List<UserResponseDTO> houseHeads = new ArrayList<>();


    @Override
    public String toString() {
        return "HouseholdResponseDTO{" +
                "id=" + id +
                ", houseHeads=" + houseHeads +
                '}';
    }
}
