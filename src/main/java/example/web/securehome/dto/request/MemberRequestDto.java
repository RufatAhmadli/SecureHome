package example.web.securehome.dto.request;

import example.web.securehome.enums.HomeMemberRole;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestDto {
    @NotNull
    private HomeMemberRole role;
}
