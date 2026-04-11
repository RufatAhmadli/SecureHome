package example.web.securehome.protocol;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DeviceCommandTest {

    // ── CommandType ───────────────────────────────────────────────────

    @Test
    void commandType_hasThreeValues() {
        assertThat(CommandType.values()).hasSize(3);
    }

    @Test
    void commandType_containsExpectedValues() {
        assertThat(CommandType.values())
                .containsExactlyInAnyOrder(
                        CommandType.STATE_CHANGE,
                        CommandType.TELEMETRY,
                        CommandType.HEARTBEAT
                );
    }

    // ── RawMessage ────────────────────────────────────────────────────

    @Test
    void rawMessage_buildsWithAllFields() {
        RawMessage msg = RawMessage.builder()
                .topic("home/1/lock/7/status")
                .payload("{\"status\":\"LOCKED\"}".getBytes())
                .header("qos", "1")
                .receivedAt(Instant.now())
                .build();

        assertThat(msg.getTopic()).isEqualTo("home/1/lock/7/status");
        assertThat(new String(msg.getPayload())).contains("LOCKED");
        assertThat(msg.getHeaders()).containsKey("qos");
    }

    @Test
    void rawMessage_headersDefaultToEmptyMap() {
        RawMessage msg = RawMessage.builder()
                .topic("home/1/lock/7/status")
                .payload(new byte[0])
                .receivedAt(Instant.now())
                .build();

        assertThat(msg.getHeaders()).isNotNull().isEmpty();
    }

    // ── DeviceCommand ─────────────────────────────────────────────────

    @Test
    void deviceCommand_buildsWithRequiredFields() {
        DeviceCommand cmd = DeviceCommand.builder()
                .deviceId(7L)
                .deviceName("lock-001")
                .type(CommandType.STATE_CHANGE)
                .action("LOCKED")
                .occurredAt(Instant.now())
                .build();

        assertThat(cmd.getDeviceId()).isEqualTo(7L);
        assertThat(cmd.getDeviceName()).isEqualTo("lock-001");
        assertThat(cmd.getType()).isEqualTo(CommandType.STATE_CHANGE);
        assertThat(cmd.getAction()).isEqualTo("LOCKED");
    }

    @Test
    void deviceCommand_payloadDefaultsToEmptyMap() {
        DeviceCommand cmd = DeviceCommand.builder()
                .deviceId(1L)
                .deviceName("cam-001")
                .type(CommandType.TELEMETRY)
                .action("BATTERY_LOW")
                .occurredAt(Instant.now())
                .build();

        assertThat(cmd.getPayload()).isNotNull().isEmpty();
    }

    @Test
    void deviceCommand_payloadCanCarryArbitraryData() {
        DeviceCommand cmd = DeviceCommand.builder()
                .deviceId(1L)
                .deviceName("cam-001")
                .type(CommandType.TELEMETRY)
                .action("BATTERY_LOW")
                .payload(Map.of("battery", 12, "unit", "percent"))
                .occurredAt(Instant.now())
                .build();

        assertThat(cmd.getPayload()).containsEntry("battery", 12);
    }
}
