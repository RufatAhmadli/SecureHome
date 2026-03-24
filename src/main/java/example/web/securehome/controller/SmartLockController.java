package example.web.securehome.controller;

import example.web.securehome.dto.request.SmartLockRequestDto;
import example.web.securehome.dto.response.SmartLockResponseDto;
import example.web.securehome.enums.LockStatus;
import example.web.securehome.service.SmartLockService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/smart-locks")
@RequiredArgsConstructor
public class SmartLockController {
    private final SmartLockService smartLockService;

    @GetMapping("/{id}")
    public ResponseEntity<SmartLockResponseDto> getSmartLockById(@PathVariable Long id) {
        return ResponseEntity.ok(smartLockService.findById(id));
    }

    @GetMapping("/home/{homeId}")
    public ResponseEntity<List<SmartLockResponseDto>> getAllSmartLocksByHomeId(@PathVariable Long homeId) {
        return ResponseEntity.ok(smartLockService.findAllByHomeId(homeId));
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<SmartLockResponseDto>> getAllSmartLocksByRoomId(@PathVariable Long roomId) {
        return ResponseEntity.ok(smartLockService.findAllByRoomId(roomId));
    }

    @PostMapping
    public ResponseEntity<SmartLockResponseDto> createSmartLock(
            @Valid @RequestBody SmartLockRequestDto smartLockRequestDto
    ) {
        return new ResponseEntity<>(smartLockService.create(smartLockRequestDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SmartLockResponseDto> updateSmartLock(
            @PathVariable Long id,
            @Valid @RequestBody SmartLockRequestDto smartLockRequestDto
    ) {
        return ResponseEntity.ok(smartLockService.update(id, smartLockRequestDto));
    }

    @PatchMapping("/{id}/lock")
    public ResponseEntity<SmartLockResponseDto> lockSmartLock(@PathVariable Long id) {
        return ResponseEntity.ok(smartLockService.lock(id));
    }

    @PatchMapping("/{id}/unlock")
    public ResponseEntity<SmartLockResponseDto> unlockSmartLock(@PathVariable Long id) {
        return ResponseEntity.ok(smartLockService.unlock(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<SmartLockResponseDto> updateLockStatus(
            @PathVariable Long id,
            @RequestParam LockStatus lockStatus
    ) {
        return ResponseEntity.ok(smartLockService.updateLockStatus(id, lockStatus));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSmartLock(@PathVariable Long id) {
        smartLockService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
