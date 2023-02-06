package rs.securehome.admin.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@Table(name = "blacklisted_jwts")
@Entity
public class BlacklistedJWT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "jwt", length = 1000)
    private String jwt;

    public BlacklistedJWT() {

    }

    public BlacklistedJWT(String jwt) {
        this.jwt = jwt;
    }
}
