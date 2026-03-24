package example.web.securehome.dto.response;

import example.web.securehome.entity.Home;
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
public class DeviceResponseDto {
    private Long id;
    private String deviceName;
    private String displayName;
    private CommunicationProtocol protocol;
    private HomeResponseDto home;
    private RoomResponseDto room;
}
