package example.web.securehome.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDto {
    private Long id;
    private String address;
    private String city;
    private String timezone;
    private String phoneNumber;
    private LocalDate birthDate;
    private Boolean emailNotifications;
    private Boolean smsNotifications;
}
