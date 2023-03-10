package rs.securehome.admin.model;


import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "privileges")
public class Privilege implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    public Privilege() {

    }

    public Privilege(String name) {
        this.name = name;
    }



    @Override
    public String getAuthority() {
        return this.name;
    }
}
