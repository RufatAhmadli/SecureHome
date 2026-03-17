package example.web.securehome.controller;

import example.web.securehome.dto.request.HomeRequestDto;
import example.web.securehome.dto.response.HomeResponseDto;
import example.web.securehome.service.HomeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/homes")
@RequiredArgsConstructor
public class HomeController {
    private final HomeService homeService;

    @GetMapping
    public ResponseEntity<List<HomeResponseDto>> getHomes() {
        return ResponseEntity.ok(homeService.findUserAllHomes());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<HomeResponseDto>> getAllHomes() {
        return ResponseEntity.ok(homeService.findAllHomes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HomeResponseDto> getHome(@PathVariable Long id) {
        return ResponseEntity.ok(homeService.findHome(id));
    }

    @PostMapping
    public ResponseEntity<HomeResponseDto> createHome(@Valid @RequestBody HomeRequestDto homeRequestDto) {
        return new ResponseEntity<>(homeService.createHome(homeRequestDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HomeResponseDto> updateHome(@PathVariable Long id, @Valid @RequestBody HomeRequestDto homeRequestDto) {
        return ResponseEntity.ok(homeService.updateHome(id, homeRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHome(@PathVariable Long id) {
        homeService.deleteHome(id);
        return ResponseEntity.noContent().build();
    }
}
