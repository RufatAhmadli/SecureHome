package example.web.securehome.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDto {
    @NotNull
    private Long id;

    @NotNull
    private String role;
    //!!! continue this part
}
