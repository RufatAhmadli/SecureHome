package example.web.securehome.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeResponseDto {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String description;
    private String timezone;


}
