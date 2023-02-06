package rs.securehome.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTenantRoleDTO {

    @Positive(message = "Id must be a positive value greater than 0.")
    private int id;

    @NotBlank(message = "Role cannot be blank")
    private String role;

    @Override
    public String toString() {
        return "UpdateTenantRoleDTO{" +
                "id=" + id +
                ", role='" + role + '\'' +
                '}';
    }
}
