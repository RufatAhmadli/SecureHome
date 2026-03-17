package example.web.securehome.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeResponseDto {
    @NotNull
    private Long id;
    @NotBlank
    private String name;

    private String address;
    private String city;
    private String description;
    private String timezone;


}
