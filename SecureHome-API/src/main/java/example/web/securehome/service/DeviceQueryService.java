package example.web.securehome.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceQueryService {

    private final DeviceRepository deviceRepository;
    private final HomeRepository homeRepository;
    private final RoomRepository roomRepository;
    private final MemberRepository memberRepository;
    private final SecurityUtils securityUtils;
    private final HomeMapper homeMapper;
    private final RoomMapper roomMapper;

    @Transactional(readOnly = true)
    public DeviceResponseDto findById(Long id) {
        User currentUser = securityUtils.getCurrentUser();
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));
        verifyHomeAccess(currentUser.getId(), device.getHome().getId());
        return toResponseDto(device);
    }

    @Transactional(readOnly = true)
    public List<DeviceResponseDto> findAllByHomeId(Long homeId) {
        User currentUser = securityUtils.getCurrentUser();
        if (!homeRepository.existsById(homeId)) throw new HomeNotFoundException(homeId);
        verifyHomeAccess(currentUser.getId(), homeId);
        return deviceRepository.findAllByHomeId(homeId).stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DeviceResponseDto> findAllByRoomId(Long roomId) {
        User currentUser = securityUtils.getCurrentUser();
        var room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));
        verifyHomeAccess(currentUser.getId(), room.getHome().getId());
        return deviceRepository.findAllByRoomId(roomId).stream()
                .map(this::toResponseDto)
                .toList();
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
