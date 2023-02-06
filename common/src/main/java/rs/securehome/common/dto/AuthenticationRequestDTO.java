package rs.securehome.common.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequestDTO {


    @Email(message = "Email must be valid.")
    @NotBlank(message = "Email cannot be blank.")
    private String email;


    @NotBlank(message = "Password cannot be blank.")
    private String password;

    @Override
    public String toString() {
        return "AuthenticationRequestDTO{" +
                "email='" + email + '\'' +
                '}';
    }
}
