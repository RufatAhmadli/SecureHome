package example.web.securehome.protocol;

import example.web.securehome.entity.Device;
import example.web.securehome.enums.DeviceStatus;
import example.web.securehome.enums.LockStatus;
import example.web.securehome.event.SecurityEvent;
import example.web.securehome.exception.custom.DeviceNotFoundException;
import example.web.securehome.repository.DeviceRepository;
import example.web.securehome.service.CameraService;
import example.web.securehome.service.SmartLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Dispatches a normalized DeviceCommand to the appropriate service.
 * Protocol-blind: only speaks DeviceCommand regardless of which adapter produced it.
 * <p>
 * Routing logic:
 * STATE_CHANGE → update device-specific state (lock status, armed flag, connectivity)
 * HEARTBEAT    → updateDeviceStatus(ONLINE)
 * TELEMETRY    → logged only (no DB state change for now)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceCommandRouter {

    private final SmartLockService smartLockService;
    private final CameraService cameraService;
    private final DeviceRepository deviceRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ApplicationEventPublisher eventPublisher;

    public void route(DeviceCommand command) {
        log.info("Routing command → homeId={} deviceId={} type={} action={}",
                command.getHomeId(), command.getDeviceId(), command.getType(), command.getAction());

        Device device = deviceRepository.findById(command.getDeviceId())
                .orElseThrow(() -> new DeviceNotFoundException(command.getDeviceId()));

        if (!device.getHome().getId().equals(command.getHomeId())) {
            String detail = "Unauthorized command rejected — device " + command.getDeviceId()
                    + " does not belong to home " + command.getHomeId();
            log.warn(detail);
            eventPublisher.publishEvent(new SecurityEvent(
                    command.getHomeId(), SecurityEvent.Action.UNAUTHORIZED_DEVICE_COMMAND, detail));
            broadcastToHome(command.getHomeId());
            return;
        }

        switch (command.getType()) {
            case STATE_CHANGE -> handleStateChange(command);
            case HEARTBEAT -> updateDeviceStatus(command.getDeviceId(), DeviceStatus.ONLINE);
            case TELEMETRY -> log.debug("Telemetry for device {}: {}",
                    command.getDeviceId(), command.getPayload());
        }

        broadcastToHome(command.getHomeId());
    }

    private void handleStateChange(DeviceCommand cmd) {
        switch (cmd.getAction()) {
            case "LOCKED" -> smartLockService.reportLockStatus(cmd.getDeviceId(), LockStatus.LOCKED);
            case "UNLOCKED" -> smartLockService.reportLockStatus(cmd.getDeviceId(), LockStatus.UNLOCKED);
            case "ARMED" -> cameraService.reportArmedStatus(cmd.getDeviceId(), true);
            case "DISARMED" -> cameraService.reportArmedStatus(cmd.getDeviceId(), false);
            case "ONLINE" -> updateDeviceStatus(cmd.getDeviceId(), DeviceStatus.ONLINE);
            case "OFFLINE" -> updateDeviceStatus(cmd.getDeviceId(), DeviceStatus.OFFLINE);
            case "ERROR" -> updateDeviceStatus(cmd.getDeviceId(), DeviceStatus.ERROR);
            default -> log.warn("Unhandled action '{}' for device {}",
                    cmd.getAction(), cmd.getDeviceId());
        }
    }

    private void updateDeviceStatus(Long deviceId, DeviceStatus status) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new DeviceNotFoundException(deviceId));
        device.setStatus(status);
        deviceRepository.save(device);
    }

    private void broadcastToHome(Long homeId) {
        messagingTemplate.convertAndSend("/topic/home/" + homeId, "refresh");
        log.debug("WebSocket broadcast → /topic/home/{}", homeId);
    }
}
