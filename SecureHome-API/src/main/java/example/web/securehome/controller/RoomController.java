package example.web.securehome.controller;

import example.web.securehome.dto.request.RoomRequestDto;
import example.web.securehome.dto.response.RoomResponseDto;
import example.web.securehome.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @GetMapping("/{homeId}")
    public ResponseEntity<RoomResponseDto> getRoomById(@PathVariable Long homeId) {
        return ResponseEntity.ok(roomService.findRoomById(homeId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<RoomResponseDto>> getAllRooms() {
        return ResponseEntity.ok(roomService.findAllRooms());
    }

    @PostMapping
    public ResponseEntity<RoomResponseDto> createRoom(@Valid @RequestBody RoomRequestDto roomRequestDto) {
        return new ResponseEntity<>(roomService.createRoom(roomRequestDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponseDto> updateRoom(@PathVariable Long id, @Valid @RequestBody RoomRequestDto roomRequestDto) {
        return ResponseEntity.ok(roomService.updateRoom(id, roomRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

}
