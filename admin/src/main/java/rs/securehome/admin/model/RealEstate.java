package rs.securehome.admin.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "real_estate")
public class RealEstate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<User> canBeSeenBy = new HashSet<>();

    @Column(name = "device_reading_period", nullable = false)
    private Integer deviceReadingPeriod;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "realEstate")
    private Set<Device> devices = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Household household;

    public RealEstate() {}

    public RealEstate(String name) {
        this.name = name;
    }

    public void addDevice(Device device) {
        devices.add(device);
        device.setRealEstate(this);
    }

    public void removeDevice(Device device) {
        devices.remove(device);
        device.setRealEstate(null);
    }
}
