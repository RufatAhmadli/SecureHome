package example.web.securehome.protocol;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

@Getter
@Builder
public class DeviceCommand {

    /** Database ID of the device that sent this message. */
    private final Long deviceId;

    /** Hardware identifier (e.g. "lock-001", "cam-front-door"). */
    private final String deviceName;

    /** Classifies the intent of this message. */
    private final CommandType type;

    /**
     * Semantic action label in SCREAMING_SNAKE_CASE.
     * Examples: LOCKED, UNLOCKED, ARMED, DISARMED, ONLINE, OFFLINE,
     *           MOTION_DETECTED, TAMPER_ALERT, BATTERY_LOW.
     * String (not enum) so new device types can add actions without modifying this class.
     */
    private final String action;

    /**
     * Extra data carried with the message.
     * Examples: {"battery": 87}, {"temperature": 22.4}.
     * Defaults to empty map when not provided.
     */
    @Builder.Default
    private final Map<String, Object> payload = Collections.emptyMap();

    /** When the event occurred on the device side. */
    private final Instant occurredAt;
}
