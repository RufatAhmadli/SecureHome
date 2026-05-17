package example.web.securehome.protocol;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.time.Instant;
import java.util.Map;

@Getter
@Builder
public class RawMessage {

    /**
     * MQTT topic path or equivalent protocol address.
     */
    private final String topic;

    /**
     * Raw message bytes. The adapter is responsible for parsing.
     */
    private final byte[] payload;

    /**
     * Optional metadata: QoS level, retain flag, protocol-specific headers.
     * Defaults to an empty map when not provided.
     */
    @Singular("header")
    private final Map<String, String> headers;

    /**
     * Timestamp when the broker received this message.
     */
    private final Instant receivedAt;
}
