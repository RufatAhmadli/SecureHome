package example.web.securehome.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponseDto {
    private Long id;
    private String roomName;
    private Integer floor;
    private String description;
    private HomeResponseDto home;
}
