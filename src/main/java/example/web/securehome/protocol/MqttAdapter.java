package example.web.securehome.protocol;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import example.web.securehome.enums.CommunicationProtocol;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Translates raw MQTT messages into normalized DeviceCommands.
 * <p>
 * Expected topic format:
 * home/{homeId}/{deviceType}/{deviceId}/{event}
 * <p>
 * Examples:
 * home/1/lock/7/status     payload: {"status":"LOCKED"}
 * home/1/camera/3/status   payload: {"armed":true}
 * home/1/device/5/status   payload: {"online":false}
 * home/1/device/5/telemetry  payload: {"battery":87}
 * home/1/device/5/heartbeat  payload: {}
 */
@Slf4j
@Component
public class MqttAdapter implements ProtocolAdapter {

    // home/{homeId}/{deviceType}/{deviceId}/{event}
    private static final Pattern TOPIC_PATTERN =
            Pattern.compile("home/(\\d+)/(\\w+)/(\\d+)/(\\w+)");

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public DeviceCommand normalize(RawMessage raw) {
        Matcher matcher = TOPIC_PATTERN.matcher(raw.getTopic());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Unrecognised MQTT topic format: " + raw.getTopic());
        }

        long deviceId = Long.parseLong(matcher.group(3));
        String deviceType = matcher.group(2);  // lock, camera, device
        String event = matcher.group(4);  // status, telemetry, heartbeat

        Map<String, Object> payload = parsePayload(raw.getPayload());
        CommandType type = resolveCommandType(event);
        String action = resolveAction(event, deviceType, payload);

        log.debug("MQTT normalised → deviceId={} type={} action={}", deviceId, type, action);

        return DeviceCommand.builder()
                .deviceId(deviceId)
                .deviceName(deviceType + "-" + deviceId)
                .type(type)
                .action(action)
                .payload(payload)
                .occurredAt(raw.getReceivedAt())
                .build();
    }

    @Override
    public CommunicationProtocol supports() {
        return CommunicationProtocol.MQTT;
    }

    // ── private helpers ───────────────────────────────────────────────

    private Map<String, Object> parsePayload(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return Map.of();
        try {
            return objectMapper.readValue(bytes, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.warn("Could not parse MQTT payload as JSON: {}", new String(bytes));
            return Map.of();
        }
    }

    private CommandType resolveCommandType(String event) {
        return switch (event) {
            case "telemetry" -> CommandType.TELEMETRY;
            case "heartbeat" -> CommandType.HEARTBEAT;
            default -> CommandType.STATE_CHANGE;
        };
    }

    private String resolveAction(String event, String deviceType, Map<String, Object> payload) {
        // Heartbeat and telemetry don't carry state-change actions
        if (event.equals("heartbeat")) return "HEARTBEAT";
        if (event.equals("telemetry")) return resolveTelemetryAction(payload);

        // status event — derive action from payload content
        if (payload.containsKey("status")) {
            return payload.get("status").toString().toUpperCase();
        }
        if (payload.containsKey("armed")) {
            return Boolean.TRUE.equals(payload.get("armed")) ? "ARMED" : "DISARMED";
        }
        if (payload.containsKey("online")) {
            return Boolean.TRUE.equals(payload.get("online")) ? "ONLINE" : "OFFLINE";
        }

        log.warn("Cannot resolve action for deviceType={} payload={}", deviceType, payload);
        return "UNKNOWN";
    }

    private String resolveTelemetryAction(Map<String, Object> payload) {
        if (payload.containsKey("battery")) return "BATTERY_REPORT";
        if (payload.containsKey("temperature")) return "TEMPERATURE_REPORT";
        if (payload.containsKey("motion")) return "MOTION_DETECTED";
        return "TELEMETRY_REPORT";
    }
}
