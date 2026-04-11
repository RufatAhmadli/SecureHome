package example.web.securehome.protocol;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Connects to the Mosquitto broker on startup and subscribes to all home device topics.
 * Every incoming MQTT message is wrapped in a RawMessage and passed through MqttAdapter
 * to produce a normalized DeviceCommand.
 *
 * Topic subscription: home/#
 * Matches: home/{homeId}/{deviceType}/{deviceId}/{event}
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MqttSubscriber {

    @Value("${mqtt.broker.url}")
    private String brokerUrl;

    @Value("${mqtt.client.id}")
    private String clientId;

    @Value("${mqtt.topic.subscribe}")
    private String topicFilter;

    private final MqttAdapter         mqttAdapter;
    private final DeviceCommandRouter commandRouter;

    private MqttClient client;

    @PostConstruct
    public void connect() {
        try {
            client = new MqttClient(brokerUrl, clientId);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);
            options.setConnectionTimeout(10);

            client.setCallback(new MqttCallback() {

                @Override
                public void connectionLost(Throwable cause) {
                    log.warn("MQTT connection lost: {}", cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    handleMessage(topic, message);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // not used — we only subscribe, never publish
                }
            });

            client.connect(options);
            client.subscribe(topicFilter, 1);

            log.info("MQTT subscriber connected to {} and subscribed to '{}'", brokerUrl, topicFilter);

        } catch (MqttException e) {
            // App still starts — MQTT is non-critical for REST API operation
            log.warn("Could not connect to MQTT broker at {} — device messages will not be received. " +
                     "Start the broker with: docker compose up -d  (reason: {})", brokerUrl, e.getMessage());
        }
    }

    @PreDestroy
    public void disconnect() {
        if (client != null && client.isConnected()) {
            try {
                client.disconnect();
                log.info("MQTT subscriber disconnected.");
            } catch (MqttException e) {
                log.warn("Error disconnecting MQTT client: {}", e.getMessage());
            }
        }
    }

    private void handleMessage(String topic, MqttMessage message) {
        try {
            RawMessage raw = RawMessage.builder()
                    .topic(topic)
                    .payload(message.getPayload())
                    .header("qos",    String.valueOf(message.getQos()))
                    .header("retain", String.valueOf(message.isRetained()))
                    .receivedAt(Instant.now())
                    .build();

            DeviceCommand command = mqttAdapter.normalize(raw);

            commandRouter.route(command);

        } catch (Exception e) {
            log.error("Failed to process MQTT message on topic '{}': {}", topic, e.getMessage());
        }
    }
}
