package example.web.securehome.controller;

import example.web.securehome.dto.request.CameraRequestDto;
import example.web.securehome.dto.response.CameraResponseDto;
import example.web.securehome.service.CameraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cameras")
@RequiredArgsConstructor
public class CameraController {
    private final CameraService cameraService;

    @GetMapping("/{id}")
    public ResponseEntity<CameraResponseDto> getCameraById(@PathVariable Long id) {
        return ResponseEntity.ok(cameraService.findById(id));
    }

    @GetMapping("/home/{homeId}")
    public ResponseEntity<List<CameraResponseDto>> getAllCamerasByHomeId(@PathVariable Long homeId) {
        return ResponseEntity.ok(cameraService.findAllByHomeId(homeId));
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<CameraResponseDto>> getAllCamerasByRoomId(@PathVariable Long roomId) {
        return ResponseEntity.ok(cameraService.findAllByRoomId(roomId));
    }

    @PostMapping
    public ResponseEntity<CameraResponseDto> createCamera(@Valid @RequestBody CameraRequestDto cameraRequestDto) {
        return new ResponseEntity<>(cameraService.create(cameraRequestDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CameraResponseDto> updateCamera(
            @PathVariable Long id,
            @Valid @RequestBody CameraRequestDto cameraRequestDto
    ) {
        return ResponseEntity.ok(cameraService.update(id, cameraRequestDto));
    }

    @PatchMapping("/{id}/arm")
    public ResponseEntity<CameraResponseDto> armCamera(@PathVariable Long id) {
        return ResponseEntity.ok(cameraService.arm(id));
    }

    @PatchMapping("/{id}/disarm")
    public ResponseEntity<CameraResponseDto> disarmCamera(@PathVariable Long id) {
        return ResponseEntity.ok(cameraService.disarm(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCamera(@PathVariable Long id) {
        cameraService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
