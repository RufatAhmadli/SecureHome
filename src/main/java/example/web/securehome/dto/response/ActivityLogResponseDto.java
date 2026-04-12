package example.web.securehome.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ActivityLogResponseDto {
    private Long id;
    private String actor;
    private String category;
    private String action;
    private String description;
    private Long homeId;
    private LocalDateTime occurredAt;
}
