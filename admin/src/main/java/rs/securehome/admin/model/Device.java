package rs.securehome.admin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "regex_filter", nullable = false)
    private String regexFilter;

    @ManyToOne(fetch = FetchType.EAGER)
    private RealEstate realEstate;

    public Device(String name, String regexFilter, RealEstate realEstate) {
        this.name = name;
        this.regexFilter = regexFilter;
        this.realEstate = realEstate;
    }
}
