package rs.securehome.common.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeDefaultPasswordRequestDTO {


    @NotBlank(message = "Old password cannot be blank.")
    private String oldPassword;


    @NotBlank(message = "New password cannot be blank.")
    private String newPassword;


    @Override
    public String toString() {
        return "ChangeDefaultPasswordRequestDTO{" +
                "oldPassword='" + oldPassword + '\'' +
                ", newPassword='" + newPassword + '\'' +
                '}';
    }
}
