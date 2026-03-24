package example.web.securehome.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequestDto {
    @NotBlank
    private String roomName;

    private Integer floor;
    private String description;

    @NotNull
    private Long homeId;
}
