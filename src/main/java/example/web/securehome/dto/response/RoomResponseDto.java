package example.web.securehome.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponseDto {
    @NotNull
    private Long id;
    @NotBlank
    private String roomName;

    private Integer floor;
    private String description;

    @NotNull
    private HomeResponseDto home;
}
