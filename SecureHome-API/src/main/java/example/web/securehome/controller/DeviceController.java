package example.web.securehome.controller;

import example.web.securehome.dto.response.DeviceResponseDto;
import example.web.securehome.service.DeviceQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceQueryService deviceQueryService;

    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponseDto> getDeviceById(@PathVariable Long id) {
        return ResponseEntity.ok(deviceQueryService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<DeviceResponseDto>> getDevices(
            @RequestParam(required = false) Long homeId,
            @RequestParam(required = false) Long roomId
    ) {
        if (homeId != null && roomId != null) {
            throw new ResponseStatusException(BAD_REQUEST, "Provide only one filter: homeId or roomId.");
        }
        if (homeId == null && roomId == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Provide one filter: homeId or roomId.");
        }
        if (homeId != null) {
            return ResponseEntity.ok(deviceQueryService.findAllByHomeId(homeId));
        }
        return ResponseEntity.ok(deviceQueryService.findAllByRoomId(roomId));
    }
}
