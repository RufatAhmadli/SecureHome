package example.web.securehome.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeRequestDto {
    @NotBlank
    private String name;

    private String address;
    private String city;
    private String description;
    private String timezone;
}
