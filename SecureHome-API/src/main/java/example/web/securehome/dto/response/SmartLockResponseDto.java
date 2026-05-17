package example.web.securehome.dto.response;

import example.web.securehome.enums.LockStatus;
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
public class SmartLockResponseDto extends DeviceResponseDto {
    private LockStatus lockStatus;
    private Boolean autoLock;
    private Integer autoLockDelaySeconds;
    private Boolean tamperAlert;

}
