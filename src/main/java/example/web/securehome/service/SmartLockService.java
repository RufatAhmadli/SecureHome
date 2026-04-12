package example.web.securehome.service;

import example.web.securehome.dto.request.SmartLockRequestDto;
import example.web.securehome.dto.response.SmartLockResponseDto;
import example.web.securehome.entity.SmartLock;
import example.web.securehome.enums.LockStatus;
import example.web.securehome.event.SmartLockEvent;
import example.web.securehome.exception.custom.DeviceNotFoundException;
import example.web.securehome.mapper.SmartLockMapper;
import example.web.securehome.repository.HomeRepository;
import example.web.securehome.repository.MemberRepository;
import example.web.securehome.repository.RoomRepository;
import example.web.securehome.repository.SmartLockRepository;
import example.web.securehome.util.SecurityUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SmartLockService extends DeviceService<SmartLock, SmartLockRequestDto, SmartLockResponseDto> {

    private final SmartLockRepository smartLockRepository;

    public SmartLockService(RoomRepository roomRepository,
                            SmartLockMapper smartLockMapper,
                            HomeRepository homeRepository,
                            MemberRepository memberRepository,
                            SecurityUtils securityUtils,
                            ApplicationEventPublisher eventPublisher,
                            SmartLockRepository smartLockRepository) {
        super(roomRepository, smartLockMapper, homeRepository, memberRepository, securityUtils, eventPublisher);
        this.smartLockRepository = smartLockRepository;
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
        SmartLock smartLock = smartLockRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));
        verifyCanOperateDevice(securityUtils.getCurrentUser(), smartLock.getHome().getId());
        smartLock.setLockStatus(lockStatus);
        return deviceMapper.toResponseDto(smartLockRepository.save(smartLock));
    }

    /**
     * Called by the MQTT router when a device reports its own lock state.
     * No user authentication or RBAC — the device itself is the source of truth.
     */
    @Transactional
    public void reportLockStatus(Long id, LockStatus lockStatus) {
        SmartLock lock = smartLockRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));
        lock.setLockStatus(lockStatus);
        smartLockRepository.save(lock);
        SmartLockEvent.Action action = lockStatus == LockStatus.LOCKED
                ? SmartLockEvent.Action.LOCKED : SmartLockEvent.Action.UNLOCKED;
        eventPublisher.publishEvent(new SmartLockEvent(lock.getDisplayName(), id, lock.getDisplayName(), lock.getHome().getId(), action));
    }

    @Transactional
    public SmartLockResponseDto lock(Long id) {
        SmartLockResponseDto result = updateLockStatus(id, LockStatus.LOCKED);
        SmartLock smartLock = smartLockRepository.findById(id).orElseThrow(() -> new DeviceNotFoundException(id));
        eventPublisher.publishEvent(new SmartLockEvent(
                securityUtils.getCurrentUser().getEmail(), id, smartLock.getDisplayName(), smartLock.getHome().getId(), SmartLockEvent.Action.LOCKED));
        return result;
    }

    @Transactional
    public SmartLockResponseDto unlock(Long id) {
        SmartLockResponseDto result = updateLockStatus(id, LockStatus.UNLOCKED);
        SmartLock smartLock = smartLockRepository.findById(id).orElseThrow(() -> new DeviceNotFoundException(id));
        eventPublisher.publishEvent(new SmartLockEvent(
                securityUtils.getCurrentUser().getEmail(), id, smartLock.getDisplayName(), smartLock.getHome().getId(), SmartLockEvent.Action.UNLOCKED));
        return result;
    }
}
