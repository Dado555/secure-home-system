package rs.securehome.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private int id;

    private String email;

    private String name;

    private String surname;

    private String authority;

    @Override
    public String toString() {
        return "UserResponseDTO{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", authority='" + authority + '\'' +
                '}';
    }
}
