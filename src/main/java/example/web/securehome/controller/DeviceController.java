package example.web.securehome.controller;

import example.web.securehome.dto.response.DeviceResponseDto;
import example.web.securehome.entity.Device;
import example.web.securehome.entity.User;
import example.web.securehome.exception.custom.DeviceNotFoundException;
import example.web.securehome.exception.custom.HomeAccessDeniedException;
import example.web.securehome.exception.custom.HomeNotFoundException;
import example.web.securehome.exception.custom.RoomNotFoundException;
import example.web.securehome.mapper.HomeMapper;
import example.web.securehome.mapper.RoomMapper;
import example.web.securehome.repository.DeviceRepository;
import example.web.securehome.repository.HomeRepository;
import example.web.securehome.repository.MemberRepository;
import example.web.securehome.repository.RoomRepository;
import example.web.securehome.util.SecurityUtils;
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
    private final DeviceRepository deviceRepository;
    private final HomeRepository homeRepository;
    private final RoomRepository roomRepository;
    private final MemberRepository memberRepository;
    private final SecurityUtils securityUtils;
    private final HomeMapper homeMapper;
    private final RoomMapper roomMapper;

    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponseDto> getDeviceById(@PathVariable Long id) {
        User currentUser = securityUtils.getCurrentUser();
        Device device = deviceRepository.findById(id).orElseThrow(() -> new DeviceNotFoundException(id));
        verifyHomeAccess(currentUser.getId(), device.getHome().getId());
        return ResponseEntity.ok(toResponseDto(device));
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
            User currentUser = securityUtils.getCurrentUser();
            if (!homeRepository.existsById(homeId)) {
                throw new HomeNotFoundException(homeId);
            }
            verifyHomeAccess(currentUser.getId(), homeId);
            return ResponseEntity.ok(deviceRepository.findAllByHomeId(homeId).stream().map(this::toResponseDto).toList());
        }
        User currentUser = securityUtils.getCurrentUser();
        var room = roomRepository.findById(roomId).orElseThrow(() -> new RoomNotFoundException(roomId));
        verifyHomeAccess(currentUser.getId(), room.getHome().getId());
        return ResponseEntity.ok(deviceRepository.findAllByRoomId(roomId).stream().map(this::toResponseDto).toList());
    }

    private void verifyHomeAccess(Long userId, Long homeId) {
        if (!memberRepository.existsHomeMemberByUserIdAndHomeId(userId, homeId)) {
            throw new HomeAccessDeniedException();
        }
    }

    private DeviceResponseDto toResponseDto(Device device) {
        return DeviceResponseDto.builder()
                .id(device.getId())
                .deviceName(device.getDeviceName())
                .displayName(device.getDisplayName())
                .protocol(device.getProtocol())
                .home(homeMapper.toHomeResponseDto(device.getHome()))
                .room(device.getRoom() == null ? null : roomMapper.toRoomResponseDto(device.getRoom()))
                .build();
    }
}
