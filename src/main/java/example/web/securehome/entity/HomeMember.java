package example.web.securehome.entity;

import example.web.securehome.enums.HomeMemberRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "home_members",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "home_id"})
        }
)
public class HomeMember extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private HomeMemberRole role;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "home_id", nullable = false)
    private Home home;

}
