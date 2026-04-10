package example.web.securehome.controller;

import example.web.securehome.dto.request.UserProfileRequestDto;
import example.web.securehome.dto.response.UserProfileResponseDto;
import example.web.securehome.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDto> getMyProfile() {
        UserProfileResponseDto profile = userProfileService.getMyProfile();
        return profile != null ? ResponseEntity.ok(profile) : ResponseEntity.noContent().build();
    }

    @PostMapping("/me")
    public ResponseEntity<UserProfileResponseDto> createMyProfile(@Valid @RequestBody UserProfileRequestDto dto) {
        return new ResponseEntity<>(userProfileService.createMyProfile(dto), HttpStatus.CREATED);
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponseDto> updateMyProfile(@Valid @RequestBody UserProfileRequestDto dto) {
        return ResponseEntity.ok(userProfileService.updateMyProfile(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponseDto> getProfileById(@PathVariable Long id) {
        return ResponseEntity.ok(userProfileService.findUserProfileById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserProfileResponseDto>> getAllProfiles() {
        return ResponseEntity.ok(userProfileService.findAllUserProfiles());
    }
}
