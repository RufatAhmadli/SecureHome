package example.web.securehome.dto.request;

import example.web.securehome.enums.CommunicationProtocol;
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
public class DeviceRequestDto {
    @NotBlank
    private String deviceName;

    @NotBlank
    private String displayName;

    @NotNull
    private CommunicationProtocol protocol;

    @NotNull
    private Long homeId;

    private Long roomId;
}
