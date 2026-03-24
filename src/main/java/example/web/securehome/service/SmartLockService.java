package example.web.securehome.service;

import example.web.securehome.dto.request.SmartLockRequestDto;
import example.web.securehome.dto.response.SmartLockResponseDto;
import example.web.securehome.entity.SmartLock;
import example.web.securehome.entity.User;
import example.web.securehome.enums.LockStatus;
import example.web.securehome.exception.custom.DeviceNotFoundException;
import example.web.securehome.exception.custom.HomeAccessDeniedException;
import example.web.securehome.mapper.SmartLockMapper;
import example.web.securehome.repository.HomeRepository;
import example.web.securehome.repository.MemberRepository;
import example.web.securehome.repository.RoomRepository;
import example.web.securehome.repository.SmartLockRepository;
import example.web.securehome.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SmartLockService extends DeviceService<SmartLock, SmartLockRequestDto, SmartLockResponseDto> {
    private final SmartLockRepository smartLockRepository;
    private final SmartLockMapper smartLockMapper;
    private final MemberRepository memberRepository;
    private final SecurityUtils securityUtils;

    public SmartLockService(RoomRepository roomRepository,
                            SmartLockMapper smartLockMapper,
                            HomeRepository homeRepository,
                            MemberRepository memberRepository,
                            SecurityUtils securityUtils,
                            SmartLockRepository smartLockRepository) {
        super(
                roomRepository,
                smartLockMapper,
                homeRepository,
                memberRepository,
                securityUtils
        );
        this.smartLockRepository = smartLockRepository;
        this.smartLockMapper = smartLockMapper;
        this.memberRepository = memberRepository;
        this.securityUtils = securityUtils;
    }

    @Override
    protected List<SmartLock> findAllByRoomIdTyped(Long roomId) {
        return smartLockRepository.findAllByRoomId(roomId);
    }

    @Override
    protected List<SmartLock> findAllByHomeIdTyped(Long homeId) {
        return smartLockRepository.findAllByHomeId(homeId);
    }

    @Override
    protected Optional<SmartLock> findByIdTyped(Long id) {
        return smartLockRepository.findById(id);
    }

    @Override
    protected SmartLock saveTyped(SmartLock entity) {
        return smartLockRepository.save(entity);
    }

    @Override
    protected void deleteTyped(SmartLock entity) {
        smartLockRepository.delete(entity);
    }

    @Transactional
    public SmartLockResponseDto updateLockStatus(Long id, LockStatus lockStatus) {
        User currentUser = securityUtils.getCurrentUser();
        SmartLock smartLock = smartLockRepository.findById(id).orElseThrow(() -> new DeviceNotFoundException(id));
        verifyHomeAccess(currentUser.getId(), smartLock.getHome().getId());
        smartLock.setLockStatus(lockStatus);
        return smartLockMapper.toResponseDto(smartLockRepository.save(smartLock));
    }

    @Transactional
    public SmartLockResponseDto lock(Long id) {
        return updateLockStatus(id, LockStatus.LOCKED);
    }

    @Transactional
    public SmartLockResponseDto unlock(Long id) {
        return updateLockStatus(id, LockStatus.UNLOCKED);
    }

    private void verifyHomeAccess(Long userId, Long homeId) {
        if (!memberRepository.existsHomeMemberByUserIdAndHomeId(userId, homeId)) {
            throw new HomeAccessDeniedException();
        }
    }
}
