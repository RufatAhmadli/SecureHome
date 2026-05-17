package example.web.securehome.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cameras")
public class Camera extends Device {
    @Column(nullable = false)
    private String resolution;

    @Column(nullable = false)
    private boolean motionDetection;

    @Column(nullable = false)
    private boolean nightVision;

    private String storageLocation;

    @Column(nullable = false)
    private boolean armed;
}
