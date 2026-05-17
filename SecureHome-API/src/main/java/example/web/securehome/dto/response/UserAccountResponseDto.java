package example.web.securehome.dto.response;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Set<String> roles;
}
