package example.web.securehome.entity;

import example.web.securehome.enums.LockStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "locks")
public class SmartLock extends Device{
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LockStatus lockStatus;

    @Column(nullable = false)
    private boolean autoLock;

    @Column(nullable = false)
    private int autoLockDelaySeconds;

    @Column(nullable = false)
    private boolean tamperAlert;

    private LocalDateTime lastUnlockedAt;
}
