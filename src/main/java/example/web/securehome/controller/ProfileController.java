package example.web.securehome.controller;

import example.web.securehome.dto.request.UserProfileRequestDto;
import example.web.securehome.dto.response.UserProfileResponseDto;
import example.web.securehome.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class ProfileController {
    private final UserProfileService userProfileService;

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponseDto> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userProfileService.findUserProfileById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserProfileResponseDto>> getProfiles() {
        return ResponseEntity.ok(userProfileService.findAllUserProfiles());
    }

    @PostMapping
    public ResponseEntity<UserProfileResponseDto> createProfile(@RequestBody UserProfileRequestDto userProfileRequestDto) {
        UserProfileResponseDto response = userProfileService.createUserProfile(userProfileRequestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProfileResponseDto> updateProfile(@PathVariable Long id, @RequestBody UserProfileRequestDto userProfileRequestDto) {
        return ResponseEntity.ok(userProfileService.updateUserProfile(id, userProfileRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        userProfileService.deleteUserProfileById(id);
        return ResponseEntity.noContent().build();
    }
}
