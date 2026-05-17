package example.web.securehome.dto.response;

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
public class CameraResponseDto extends DeviceResponseDto {
    private String resolution;
    private Boolean motionDetection;
    private Boolean nightVision;
    private String storageLocation;
    private Boolean armed;
}
