package example.web.securehome.service;

import example.web.securehome.dto.request.CameraRequestDto;
import example.web.securehome.dto.response.CameraResponseDto;
import example.web.securehome.entity.Camera;
import example.web.securehome.event.CameraEvent;
import example.web.securehome.exception.custom.DeviceNotFoundException;
import example.web.securehome.mapper.CameraMapper;
import example.web.securehome.repository.CameraRepository;
import example.web.securehome.repository.HomeRepository;
import example.web.securehome.repository.MemberRepository;
import example.web.securehome.repository.RoomRepository;
import example.web.securehome.util.SecurityUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CameraService extends DeviceService<Camera, CameraRequestDto, CameraResponseDto> {

    private final CameraRepository cameraRepository;

    public CameraService(RoomRepository roomRepository,
                         CameraMapper cameraMapper,
                         HomeRepository homeRepository,
                         MemberRepository memberRepository,
                         SecurityUtils securityUtils,
                         ApplicationEventPublisher eventPublisher,
                         CameraRepository cameraRepository) {
        super(roomRepository, cameraMapper, homeRepository, memberRepository, securityUtils, eventPublisher);
        this.cameraRepository = cameraRepository;
    }

    @Override
    protected List<Camera> findAllByRoomIdTyped(Long roomId) {
        return cameraRepository.findAllByRoomId(roomId);
    }

    @Override
    protected List<Camera> findAllByHomeIdTyped(Long homeId) {
        return cameraRepository.findAllByHomeId(homeId);
    }

    @Override
    protected Optional<Camera> findByIdTyped(Long id) {
        return cameraRepository.findById(id);
    }

    @Override
    protected Camera saveTyped(Camera entity) {
        return cameraRepository.save(entity);
    }

    @Override
    protected void deleteTyped(Camera entity) {
        cameraRepository.delete(entity);
    }

    @Transactional
    public CameraResponseDto arm(Long id) {
        Camera camera = cameraRepository.findById(id).orElseThrow(() -> new DeviceNotFoundException(id));
        verifyCanManageDevices(securityUtils.getCurrentUser(), camera.getHome().getId());
        camera.setArmed(true);
        Camera saved = cameraRepository.save(camera);
        eventPublisher.publishEvent(new CameraEvent(
                securityUtils.getCurrentUser().getEmail(), id, camera.getDisplayName(), camera.getHome().getId(), CameraEvent.Action.ARMED));
        return deviceMapper.toResponseDto(saved);
    }

    @Transactional
    public CameraResponseDto disarm(Long id) {
        Camera camera = cameraRepository.findById(id).orElseThrow(() -> new DeviceNotFoundException(id));
        verifyCanManageDevices(securityUtils.getCurrentUser(), camera.getHome().getId());
        camera.setArmed(false);
        Camera saved = cameraRepository.save(camera);
        eventPublisher.publishEvent(new CameraEvent(
                securityUtils.getCurrentUser().getEmail(), id, camera.getDisplayName(), camera.getHome().getId(), CameraEvent.Action.DISARMED));
        return deviceMapper.toResponseDto(saved);
    }
}
