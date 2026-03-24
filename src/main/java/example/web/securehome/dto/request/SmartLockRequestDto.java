package example.web.securehome.dto.request;

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
public class SmartLockRequestDto extends DeviceRequestDto {
    @NotNull
    private Boolean autoLock;

    @NotNull
    private Integer autoLockDelaySeconds;

    @NotNull
    private Boolean tamperAlert;
}
