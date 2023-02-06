package rs.securehome.common.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponseDTO {

    private String jwt;

    @Override
    public String toString() {
        return "AuthenticationResponseDTO{" +
                "jwt='" + jwt + '\'' +
                '}';
    }
}
