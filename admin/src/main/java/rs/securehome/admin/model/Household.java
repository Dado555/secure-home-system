package rs.securehome.admin.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "households")
public class Household {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "household")
    private List<User> tenants = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "household")
    private Set<RealEstate> realEstate = new HashSet<>();

    public Household() {}

    public void addTenant(User tenant) {
        tenants.add(tenant);
        tenant.setHousehold(this);
    }

    public void removeTenant(User tenant) {
        tenants.remove(tenant);
        tenant.setHousehold(null);
    }

    public void addObject(RealEstate object) {
        realEstate.add(object);
        object.setHousehold(this);
    }

    public void removeObject(RealEstate object) {
        realEstate.remove(object);
        object.setHousehold(null);
    }
}
