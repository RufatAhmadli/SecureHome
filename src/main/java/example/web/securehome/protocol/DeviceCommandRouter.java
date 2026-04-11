package example.web.securehome.protocol;

import example.web.securehome.entity.Device;
import example.web.securehome.enums.DeviceStatus;
import example.web.securehome.enums.LockStatus;
import example.web.securehome.exception.custom.DeviceNotFoundException;
import example.web.securehome.repository.DeviceRepository;
import example.web.securehome.service.CameraService;
import example.web.securehome.service.SmartLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Dispatches a normalized DeviceCommand to the appropriate service.
 * Protocol-blind: only speaks DeviceCommand regardless of which adapter produced it.
 *
 * Routing logic:
 *   STATE_CHANGE → update device-specific state (lock status, armed flag, connectivity)
 *   HEARTBEAT    → mark device ONLINE
 *   TELEMETRY    → logged only (no DB state change for now)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceCommandRouter {

    private final SmartLockService smartLockService;
    private final CameraService    cameraService;
    private final DeviceRepository deviceRepository;

    public void route(DeviceCommand command) {
        log.info("Routing command → deviceId={} type={} action={}",
                command.getDeviceId(), command.getType(), command.getAction());

        switch (command.getType()) {
            case STATE_CHANGE -> handleStateChange(command);
            case HEARTBEAT    -> markOnline(command.getDeviceId());
            case TELEMETRY    -> log.debug("Telemetry for device {}: {}",
                                    command.getDeviceId(), command.getPayload());
        }
    }

    private void handleStateChange(DeviceCommand cmd) {
        switch (cmd.getAction()) {
            case "LOCKED"   -> smartLockService.reportLockStatus(cmd.getDeviceId(), LockStatus.LOCKED);
            case "UNLOCKED" -> smartLockService.reportLockStatus(cmd.getDeviceId(), LockStatus.UNLOCKED);
            case "ARMED"    -> cameraService.reportArmedStatus(cmd.getDeviceId(), true);
            case "DISARMED" -> cameraService.reportArmedStatus(cmd.getDeviceId(), false);
            case "ONLINE"   -> updateDeviceStatus(cmd.getDeviceId(), DeviceStatus.ONLINE);
            case "OFFLINE"  -> updateDeviceStatus(cmd.getDeviceId(), DeviceStatus.OFFLINE);
            case "ERROR"    -> updateDeviceStatus(cmd.getDeviceId(), DeviceStatus.ERROR);
            default         -> log.warn("Unhandled action '{}' for device {}",
                                    cmd.getAction(), cmd.getDeviceId());
        }
    }

    private void markOnline(Long deviceId) {
        updateDeviceStatus(deviceId, DeviceStatus.ONLINE);
    }

    private void updateDeviceStatus(Long deviceId, DeviceStatus status) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new DeviceNotFoundException(deviceId));
        device.setStatus(status);
        deviceRepository.save(device);
    }
}
