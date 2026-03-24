package example.web.securehome.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CameraRequestDto extends DeviceRequestDto {
    @NotBlank
    private String resolution;

    @NotNull
    private Boolean motionDetection;

    @NotNull
    private Boolean nightVision;

    private String storageLocation;
}
