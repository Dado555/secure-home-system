package rs.securehome.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PrivateKeyDownloadDTO {

    @NotBlank(message = "Alias cannot be blank.")
    private String alias;

    @NotBlank(message = "Password cannot be blank.")
    private String password;

    @NotNull(message = "Token value cannot be null.")
    private UUID token;

    @Override
    public String toString() {
        return "PrivateKeyDownloadDTO{" +
                "alias='" + alias + '\'' +
                ", password='" + password + '\'' +
                ", token=" + token +
                '}';
    }
}
