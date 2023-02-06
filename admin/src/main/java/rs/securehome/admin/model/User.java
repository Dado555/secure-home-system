package rs.securehome.admin.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;


    public static final int ACCOUNT_LOCK_DURATION_DAYS = 3;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    private Household household;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "authority_id")
    private Authority authority;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, mappedBy = "user")
    private Set<FailedLoginAttempt> failedLoginAttempts = new HashSet<>();


    @Column(name = "account_locked_at")
    private LocalDateTime accountLockedAt;

    @Column(name = "default_password_changed")
    private Boolean defaultPasswordChanged = false;

    public User() {
    }

    public User(String email,
                String password,
                String name,
                String surname,
                Authority authority) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.authority = authority;
        this.accountLockedAt = null;
    }

    public User(String email,
                String password,
                String name,
                String surname,
                Authority authority,
                Boolean defaultPasswordChanged) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.authority = authority;
        this.accountLockedAt = null;
        this.defaultPasswordChanged = defaultPasswordChanged;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authority.getPrivileges();
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !this.isAccountLocked();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.isAccountLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !this.isAccountLocked();
    }

    @Override
    public boolean isEnabled() {
        return !this.isAccountLocked();
    }


    public boolean isAccountLocked() {
        if (this.accountLockedAt == null)
            return false;
        else return LocalDateTime.now().isBefore(this.accountLockedAt.plusDays(ACCOUNT_LOCK_DURATION_DAYS));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) && email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
}
