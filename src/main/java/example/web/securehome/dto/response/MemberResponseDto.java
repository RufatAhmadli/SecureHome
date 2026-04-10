package example.web.securehome.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDto {
    private Long id;
    private String role;
    private Long userId;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private Long homeId;
    private String homeName;
}
