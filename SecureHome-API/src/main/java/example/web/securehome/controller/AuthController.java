package example.web.securehome.controller;

import example.web.securehome.dto.request.ChangePasswordRequestDto;
import example.web.securehome.dto.request.LoginRequestDto;
import example.web.securehome.dto.request.RegisterRequestDto;
import example.web.securehome.dto.request.UpdateUserRequestDto;
import example.web.securehome.dto.response.LoginResponseDto;
import example.web.securehome.dto.response.RegisterResponseDto;
import example.web.securehome.dto.response.UserAccountResponseDto;
import example.web.securehome.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(@Valid @RequestBody RegisterRequestDto dto) {
        return new ResponseEntity<>(authService.register(dto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @GetMapping("/me")
    public ResponseEntity<UserAccountResponseDto> getMe() {
        return ResponseEntity.ok(authService.getMe());
    }

    @PatchMapping("/me")
    public ResponseEntity<UserAccountResponseDto> updateMyName(@Valid @RequestBody UpdateUserRequestDto dto) {
        return ResponseEntity.ok(authService.updateMyName(dto));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequestDto dto) {
        authService.changePassword(dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount() {
        authService.deleteMyAccount();
        return ResponseEntity.noContent().build();
    }
}
