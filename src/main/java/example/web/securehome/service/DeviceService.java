package example.web.securehome.service;

import example.web.securehome.dto.request.DeviceRequestDto;
import example.web.securehome.dto.response.DeviceResponseDto;
import example.web.securehome.entity.Device;
import example.web.securehome.entity.Home;
import example.web.securehome.entity.Room;
import example.web.securehome.entity.User;
import example.web.securehome.enums.DeviceStatus;
import example.web.securehome.exception.custom.DeviceNotFoundException;
import example.web.securehome.exception.custom.HomeAccessDeniedException;
import example.web.securehome.exception.custom.HomeNotFoundException;
import example.web.securehome.exception.custom.RoomNotFoundException;
import example.web.securehome.mapper.DeviceMapper;
import example.web.securehome.repository.HomeRepository;
import example.web.securehome.repository.MemberRepository;
import example.web.securehome.repository.RoomRepository;
import example.web.securehome.util.SecurityUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public abstract class DeviceService<T extends Device, REQ extends DeviceRequestDto, RES extends DeviceResponseDto> {

    private final RoomRepository roomRepository;
    private final HomeRepository homeRepository;
    private final DeviceMapper<T, REQ, RES> deviceMapper;
    private final MemberRepository memberRepository;
    private final SecurityUtils securityUtils;

    public DeviceService(RoomRepository roomRepository,
                         DeviceMapper<T, REQ, RES> deviceMapper,
                         HomeRepository homeRepository,
                         MemberRepository memberRepository,
                         SecurityUtils securityUtils) {
        this.roomRepository = roomRepository;
        this.deviceMapper = deviceMapper;
        this.homeRepository = homeRepository;
        this.memberRepository = memberRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public List<RES> findAllByRoomId(Long roomId) {
        User currentUser = securityUtils.getCurrentUser();
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));
        verifyHomeAccess(currentUser, room.getHome().getId());

        return findAllByRoomIdTyped(roomId).stream()
                .map(deviceMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RES> findAllByHomeId(Long homeId) {
        User currentUser = securityUtils.getCurrentUser();
        if (!homeRepository.existsById(homeId)) throw new HomeNotFoundException(homeId);
        verifyHomeAccess(currentUser, homeId);

        return findAllByHomeIdTyped(homeId).stream()
                .map(deviceMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public RES findById(Long id) {
        User currentUser = securityUtils.getCurrentUser();
        T device = findTypedDeviceById(id);
        verifyHomeAccess(currentUser, device.getHome().getId());
        return deviceMapper.toResponseDto(device);
    }

    @Transactional
    public RES create(REQ requestDto) {
        User currentUser = securityUtils.getCurrentUser();
        Home home = homeRepository.findById(requestDto.getHomeId())
                .orElseThrow(() -> new HomeNotFoundException(requestDto.getHomeId()));
        verifyHomeAccess(currentUser, home.getId());

        Room room = resolveRoom(requestDto.getRoomId(), home.getId());
        T device = deviceMapper.toEntity(requestDto);
        device.setHome(home);
        device.setRoom(room);
        device.setStatus(DeviceStatus.INITIALIZING);
        return deviceMapper.toResponseDto(saveTyped(device));
    }

    @Transactional
    public RES update(Long id, REQ requestDto) {
        User currentUser = securityUtils.getCurrentUser();
        T existingDevice = findTypedDeviceById(id);
        verifyHomeAccess(currentUser, existingDevice.getHome().getId());

        if (!existingDevice.getHome().getId().equals(requestDto.getHomeId())) {
            throw new HomeAccessDeniedException("Changing device home is not allowed.");
        }

        Room room = resolveRoom(requestDto.getRoomId(), requestDto.getHomeId());
        deviceMapper.updateEntity(existingDevice, requestDto);
        existingDevice.setRoom(room);
        return deviceMapper.toResponseDto(saveTyped(existingDevice));
    }

    @Transactional
    public void delete(Long id) {
        User currentUser = securityUtils.getCurrentUser();
        T existingDevice = findTypedDeviceById(id);
        verifyHomeAccess(currentUser, existingDevice.getHome().getId());
        deleteTyped(existingDevice);
    }

    private T findTypedDeviceById(Long id) {
        return findByIdTyped(id).orElseThrow(() -> new DeviceNotFoundException(id));
    }

    private Room resolveRoom(Long roomId, Long homeId) {
        if (roomId == null) {
            return null;
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));
        if (!room.getHome().getId().equals(homeId)) {
            throw new HomeAccessDeniedException("Room does not belong to the selected home.");
        }
        return room;
    }

    private void verifyHomeAccess(User currentUser, Long homeId) {
        if (!memberRepository.existsHomeMemberByUserIdAndHomeId(currentUser.getId(), homeId)) {
            throw new HomeAccessDeniedException();
        }
    }

    protected abstract List<T> findAllByRoomIdTyped(Long roomId);

    protected abstract List<T> findAllByHomeIdTyped(Long homeId);

    protected abstract java.util.Optional<T> findByIdTyped(Long id);

    protected abstract T saveTyped(T entity);

    protected abstract void deleteTyped(T entity);
}
