package example.web.securehome.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UserProfileRequestDto {

    private String address;
    private String city;
    private String timezone;
    private String phoneNumber;
    private LocalDate birthDate;
    private Boolean emailNotifications = true;
    private Boolean smsNotifications = false;
}
