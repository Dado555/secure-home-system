package rs.securehome.admin.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @Column(name = "email", nullable = false)
    protected String email;

    @Column(name = "name", nullable = false)
    protected String name;

    @Column(name = "surname", nullable = false)
    protected String surname;

    @Column(name = "organization", nullable = false)
    protected String organization;

    @Column(name = "organisation_unit", nullable = false)
    protected String organizationUnit;

    @Column(name = "country_code", nullable = false)
    protected String countryCode;

    @Column(name = "province", nullable = false)
    protected String province;

    @Column(name = "city", nullable = false)
    protected String city;

    protected BaseEntity() {}

    protected BaseEntity(String email,
                      String name,
                      String surname,
                      String organization,
                      String organizationUnit,
                      String countryCode,
                      String province,
                      String city) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.organization = organization;
        this.organizationUnit = organizationUnit;
        this.countryCode = countryCode;
        this.province = province;
        this.city = city;
    }
}
