package example.web.securehome.protocol;

import example.web.securehome.enums.CommunicationProtocol;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ProtocolAdapterContractTest {

    /**
     * Minimal fake adapter — verifies that any class implementing ProtocolAdapter
     * can produce a valid DeviceCommand from a RawMessage.
     */
    static class FakeAdapter implements ProtocolAdapter {

        @Override
        public DeviceCommand normalize(RawMessage raw) {
            return DeviceCommand.builder()
                    .deviceId(42L)
                    .deviceName("fake-device")
                    .type(CommandType.STATE_CHANGE)
                    .action("ONLINE")
                    .occurredAt(raw.getReceivedAt())
                    .build();
        }

        @Override
        public CommunicationProtocol supports() {
            return CommunicationProtocol.MQTT;
        }
    }

    @Test
    void adapter_normalizeReturnsDeviceCommand() {
        ProtocolAdapter adapter = new FakeAdapter();

        RawMessage raw = RawMessage.builder()
                .topic("home/1/device/42/status")
                .payload("{\"online\":true}".getBytes())
                .receivedAt(Instant.now())
                .build();

        DeviceCommand cmd = adapter.normalize(raw);

        assertThat(cmd).isNotNull();
        assertThat(cmd.getDeviceId()).isEqualTo(42L);
        assertThat(cmd.getType()).isEqualTo(CommandType.STATE_CHANGE);
        assertThat(cmd.getAction()).isEqualTo("ONLINE");
    }

    @Test
    void adapter_supportsReturnsProtocol() {
        ProtocolAdapter adapter = new FakeAdapter();
        assertThat(adapter.supports()).isEqualTo(CommunicationProtocol.MQTT);
    }

    @Test
    void adapter_occurredAtPropagatedFromRawMessage() {
        ProtocolAdapter adapter = new FakeAdapter();
        Instant now = Instant.now();

        RawMessage raw = RawMessage.builder()
                .topic("home/1/device/42/status")
                .payload(new byte[0])
                .receivedAt(now)
                .build();

        DeviceCommand cmd = adapter.normalize(raw);
        assertThat(cmd.getOccurredAt()).isEqualTo(now);
    }
}
