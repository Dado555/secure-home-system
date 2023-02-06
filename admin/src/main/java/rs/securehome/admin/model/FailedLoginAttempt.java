package rs.securehome.admin.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "failed_login_attempts")
public class FailedLoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "attempt_time")
    private LocalDateTime attemptTime;

    public FailedLoginAttempt() {

    }

    public FailedLoginAttempt(User user, LocalDateTime attemptTime) {
        this.user = user;
        this.attemptTime = attemptTime;
    }

}
