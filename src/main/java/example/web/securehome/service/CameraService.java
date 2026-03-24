package example.web.securehome.service;

import example.web.securehome.dto.request.CameraRequestDto;
import example.web.securehome.dto.response.CameraResponseDto;
import example.web.securehome.entity.Camera;
import example.web.securehome.mapper.CameraMapper;
import example.web.securehome.repository.CameraRepository;
import example.web.securehome.repository.HomeRepository;
import example.web.securehome.repository.MemberRepository;
import example.web.securehome.repository.RoomRepository;
import example.web.securehome.util.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CameraService extends DeviceService<Camera, CameraRequestDto, CameraResponseDto> {
    private final CameraRepository cameraRepository;

    public CameraService(
            RoomRepository roomRepository,
            CameraMapper cameraMapper,
            HomeRepository homeRepository,
            MemberRepository memberRepository,
            SecurityUtils securityUtils,
            CameraRepository cameraRepository
    ) {
        super(roomRepository, cameraMapper, homeRepository, memberRepository, securityUtils);
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
}
