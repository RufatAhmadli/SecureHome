package example.web.securehome.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "activity_logs")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String actorEmail;

    @Column(nullable = false)
    private String category;   // DEVICE, MEMBER, ROOM, SECURITY

    @Column(nullable = false)
    private String action;     // LOCKED, ADDED, CREATED, LOGGED_IN ...

    @Column(nullable = false)
    private String description;

    private Long homeId;

    @Column(nullable = false)
    private LocalDateTime occurredAt;
}
