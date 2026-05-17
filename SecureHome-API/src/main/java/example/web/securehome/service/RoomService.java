package example.web.securehome.service;

import example.web.securehome.dto.request.RoomRequestDto;
import example.web.securehome.dto.response.RoomResponseDto;
import example.web.securehome.entity.Home;
import example.web.securehome.entity.HomeMember;
import example.web.securehome.entity.Room;
import example.web.securehome.entity.User;
import example.web.securehome.exception.custom.HomeAccessDeniedException;
import example.web.securehome.exception.custom.HomeNotFoundException;
import example.web.securehome.exception.custom.RoomNotFoundException;
import example.web.securehome.repository.HomeRepository;
import example.web.securehome.mapper.RoomMapper;
import example.web.securehome.repository.MemberRepository;
import example.web.securehome.repository.RoomRepository;
import example.web.securehome.event.RoomEvent;
import example.web.securehome.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;
    private final HomeRepository homeRepository;
    private final SecurityUtils securityUtils;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public RoomResponseDto findRoomById(Long id) {
        User currentUser = securityUtils.getCurrentUser();
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException(id));

        if (!memberRepository.existsHomeMemberByUserIdAndHomeId(currentUser.getId(),
                room.getHome().getId())) throw new HomeAccessDeniedException();


        return roomMapper.toRoomResponseDto(room);
    }

    @Transactional(readOnly = true)
    public List<RoomResponseDto> findAllRoomsByHomeId(Long homeId) {
        User currentUser = securityUtils.getCurrentUser();
        if (!memberRepository.existsHomeMemberByUserIdAndHomeId(currentUser.getId(), homeId))
            throw new HomeAccessDeniedException();

        return roomRepository.findAllByHomeId(homeId)
                .stream()
                .map(roomMapper::toRoomResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RoomResponseDto> findAllRooms() {
        return roomRepository.findAll()
                .stream()
                .map(roomMapper::toRoomResponseDto)
                .toList();
    }

    @Transactional
    public RoomResponseDto createRoom(RoomRequestDto roomRequestDto) {
        Long homeId = roomRequestDto.getHomeId();
        User currentUser = securityUtils.getCurrentUser();

        HomeMember member = memberRepository.findByHomeIdAndUserId(homeId, currentUser.getId())
                .orElseThrow(HomeAccessDeniedException::new);

        if (!member.getRole().canManageRoom())
            throw new HomeAccessDeniedException("Only Owners or Admins can create rooms.");

        Room room = roomMapper.toRoomEntity(roomRequestDto);
        room.setHome(member.getHome());

        RoomResponseDto result = roomMapper.toRoomResponseDto(roomRepository.save(room));
        eventPublisher.publishEvent(new RoomEvent(
                currentUser.getEmail(), homeId, result.getId(), room.getRoomName(), RoomEvent.Action.CREATED));
        return result;
    }

    @Transactional
    public RoomResponseDto updateRoom(Long id, RoomRequestDto roomRequestDto) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException(id));

        User currentUser = securityUtils.getCurrentUser();
        Long homeId = roomRequestDto.getHomeId();

        HomeMember member = memberRepository.findByHomeIdAndUserId(homeId, currentUser.getId())
                .orElseThrow(HomeAccessDeniedException::new);
        if (!member.getRole().canManageRoom())
            throw new HomeAccessDeniedException("Only Owners or Admins can update rooms.");

        roomMapper.updateRoomEntity(room, roomRequestDto);
        RoomResponseDto result = roomMapper.toRoomResponseDto(roomRepository.save(room));
        eventPublisher.publishEvent(new RoomEvent(
                currentUser.getEmail(), roomRequestDto.getHomeId(), id, room.getRoomName(), RoomEvent.Action.UPDATED));
        return result;
    }

    @Transactional
    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException(id));

        User currentUser = securityUtils.getCurrentUser();
        HomeMember member = memberRepository.findByHomeIdAndUserId(room.getHome().getId(), currentUser.getId())
                .orElseThrow(HomeAccessDeniedException::new);

        if (!member.getRole().canManageRoom())
            throw new HomeAccessDeniedException("Only Owners or Admins can delete rooms.");

        eventPublisher.publishEvent(new RoomEvent(
                currentUser.getEmail(), room.getHome().getId(), id, room.getRoomName(), RoomEvent.Action.DELETED));
        roomRepository.deleteById(id);
    }
}
