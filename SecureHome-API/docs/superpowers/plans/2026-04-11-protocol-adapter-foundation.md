# Protocol Adapter Foundation — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Set up a local Mosquitto MQTT broker via Docker Compose, add the Paho Java client dependency, and define the base adapter abstractions (`RawMessage`, `CommandType`, `DeviceCommand`, `ProtocolAdapter`).

**Architecture:** Mosquitto runs as a standalone Docker service (the message broker). Spring Boot connects to it via the Eclipse Paho client library. All incoming device messages — regardless of protocol — are wrapped in `RawMessage` and normalized into `DeviceCommand` by classes that implement `ProtocolAdapter`. Nothing downstream of the adapter knows which protocol the message came from.

**Tech Stack:** Docker Compose, Eclipse Mosquitto, Eclipse Paho MQTT v3 client, Java 21, Spring Boot 4, Lombok.

---

## File Map

| Action | File | Responsibility |
|---|---|---|
| Create | `docker-compose.yml` | Defines Mosquitto broker service |
| Create | `tools/mosquitto/mosquitto.conf` | Mosquitto broker configuration |
| Modify | `build.gradle` | Add Paho MQTT client dependency |
| Create | `src/main/java/example/web/securehome/protocol/RawMessage.java` | Protocol-agnostic message wrapper |
| Create | `src/main/java/example/web/securehome/protocol/CommandType.java` | Enum classifying message intent |
| Create | `src/main/java/example/web/securehome/protocol/DeviceCommand.java` | Normalized internal device language |
| Create | `src/main/java/example/web/securehome/protocol/ProtocolAdapter.java` | Interface all adapters must implement |
| Create | `src/test/java/example/web/securehome/protocol/DeviceCommandTest.java` | Unit tests for DeviceCommand |
| Create | `src/test/java/example/web/securehome/protocol/ProtocolAdapterContractTest.java` | Contract test for adapter interface |

---

## Task 1: Docker Compose + Mosquitto config

**Files:**
- Create: `docker-compose.yml`
- Create: `tools/mosquitto/mosquitto.conf`

- [ ] **Step 1: Create Mosquitto config**

Create `tools/mosquitto/mosquitto.conf`:

```conf
# Listener on standard MQTT port
listener 1883

# Allow connections without username/password (dev only)
allow_anonymous true

# Log all message types to stdout
log_dest stdout
log_type all
```

- [ ] **Step 2: Create docker-compose.yml**

Create `docker-compose.yml` at project root:

```yaml
services:
  mosquitto:
    image: eclipse-mosquitto:2.0
    container_name: securehome-mosquitto
    ports:
      - "1883:1883"
    volumes:
      - ./tools/mosquitto/mosquitto.conf:/mosquitto/config/mosquitto.conf
    restart: unless-stopped
```

- [ ] **Step 3: Start the broker and verify**

```bash
docker compose up -d
docker compose logs mosquitto
```

Expected output contains:
```
mosquitto  | 1234567890: mosquitto version 2.0.x starting
mosquitto  | 1234567890: Opening ipv4 listen socket on port 1883.
mosquitto  | 1234567890: mosquitto version 2.0.x running
```

- [ ] **Step 4: Smoke test — publish and subscribe**

Open two terminals.

Terminal 1 (subscribe — listens for messages):
```bash
mosquitto_sub -h localhost -p 1883 -t "test/#" -v
```

Terminal 2 (publish — simulates a device):
```bash
mosquitto_pub -h localhost -p 1883 -t "test/device/1" -m "hello"
```

Expected: Terminal 1 prints `test/device/1 hello`

> If `mosquitto_sub`/`mosquitto_pub` are not installed locally, use Docker instead:
> ```bash
> docker run --rm --network host eclipse-mosquitto:2.0 mosquitto_pub -h localhost -p 1883 -t "test/device/1" -m "hello"
> docker run --rm --network host eclipse-mosquitto:2.0 mosquitto_sub -h localhost -p 1883 -t "test/#" -v
> ```

- [ ] **Step 5: Commit**

```bash
git add docker-compose.yml tools/mosquitto/mosquitto.conf
git commit -m "feat: add Mosquitto broker via Docker Compose"
```

---

## Task 2: Add Paho MQTT client dependency

**Files:**
- Modify: `build.gradle`

- [ ] **Step 1: Add dependency**

In `build.gradle`, inside the `dependencies { }` block, add after the existing `implementation` lines:

```groovy
implementation 'org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5'
```

The full dependencies block becomes:
```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-liquibase'
    implementation 'org.mapstruct:mapstruct:1.6.3'
    implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6'
    implementation 'org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5'
    implementation('org.postgresql:postgresql')
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    annotationProcessor 'org.mapstruct:mapstruct-processor:1.6.3'
    annotationProcessor 'org.projectlombok:lombok-mapstruct-binding:0.2.0'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.6'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.3'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}
```

- [ ] **Step 2: Verify dependency resolves**

```bash
./gradlew dependencies --configuration compileClasspath | grep paho
```

Expected output contains:
```
org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5
```

- [ ] **Step 3: Commit**

```bash
git add build.gradle
git commit -m "feat: add Eclipse Paho MQTT client dependency"
```

---

## Task 3: `CommandType` enum

**Files:**
- Create: `src/main/java/example/web/securehome/protocol/CommandType.java`

- [ ] **Step 1: Write the failing test**

Create `src/test/java/example/web/securehome/protocol/DeviceCommandTest.java`:

```java
package example.web.securehome.protocol;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class DeviceCommandTest {

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
}
```

- [ ] **Step 2: Run test to verify it fails**

```bash
./gradlew test --tests "example.web.securehome.protocol.DeviceCommandTest" 2>&1 | tail -20
```

Expected: compilation error — `CommandType` does not exist yet.

- [ ] **Step 3: Create `CommandType`**

Create `src/main/java/example/web/securehome/protocol/CommandType.java`:

```java
package example.web.securehome.protocol;

public enum CommandType {
    /** Device state changed: LOCKED, ARMED, ONLINE, OFFLINE, etc. */
    STATE_CHANGE,

    /** Sensor reading reported: battery level, temperature, signal strength. */
    TELEMETRY,

    /** Device alive ping — presence confirmation, no state change. */
    HEARTBEAT
}
```

- [ ] **Step 4: Run test to verify it passes**

```bash
./gradlew test --tests "example.web.securehome.protocol.DeviceCommandTest" 2>&1 | tail -10
```

Expected:
```
DeviceCommandTest > commandType_hasThreeValues() PASSED
DeviceCommandTest > commandType_containsExpectedValues() PASSED
```

- [ ] **Step 5: Commit**

```bash
git add src/main/java/example/web/securehome/protocol/CommandType.java \
        src/test/java/example/web/securehome/protocol/DeviceCommandTest.java
git commit -m "feat: add CommandType enum"
```

---

## Task 4: `RawMessage`

**Files:**
- Create: `src/main/java/example/web/securehome/protocol/RawMessage.java`

- [ ] **Step 1: Add test for RawMessage construction**

Append to `src/test/java/example/web/securehome/protocol/DeviceCommandTest.java`:

```java
    @Test
    void rawMessage_buildsWithAllFields() {
        RawMessage msg = RawMessage.builder()
            .topic("home/1/lock/7/status")
            .payload("{\"status\":\"LOCKED\"}".getBytes())
            .header("qos", "1")
            .receivedAt(java.time.Instant.now())
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
            .receivedAt(java.time.Instant.now())
            .build();

        assertThat(msg.getHeaders()).isNotNull().isEmpty();
    }
```

- [ ] **Step 2: Run to verify it fails**

```bash
./gradlew test --tests "example.web.securehome.protocol.DeviceCommandTest" 2>&1 | tail -10
```

Expected: compilation error — `RawMessage` does not exist yet.

- [ ] **Step 3: Create `RawMessage`**

Create `src/main/java/example/web/securehome/protocol/RawMessage.java`:

```java
package example.web.securehome.protocol;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.time.Instant;
import java.util.Map;

@Getter
@Builder
public class RawMessage {

    /** MQTT topic path or equivalent protocol address (e.g. Zigbee cluster/endpoint path). */
    private final String topic;

    /** Raw message bytes. The adapter is responsible for parsing (JSON, binary, hex, etc.). */
    private final byte[] payload;

    /**
     * Optional metadata: QoS level, retain flag, protocol-specific headers.
     * Defaults to an empty map when not provided.
     */
    @Singular("header")
    private final Map<String, String> headers;

    /** Timestamp when the broker received this message. */
    private final Instant receivedAt;
}
```

- [ ] **Step 4: Run to verify it passes**

```bash
./gradlew test --tests "example.web.securehome.protocol.DeviceCommandTest" 2>&1 | tail -15
```

Expected:
```
DeviceCommandTest > rawMessage_buildsWithAllFields() PASSED
DeviceCommandTest > rawMessage_headersDefaultToEmptyMap() PASSED
```

- [ ] **Step 5: Commit**

```bash
git add src/main/java/example/web/securehome/protocol/RawMessage.java \
        src/test/java/example/web/securehome/protocol/DeviceCommandTest.java
git commit -m "feat: add RawMessage protocol wrapper"
```

---

## Task 5: `DeviceCommand`

**Files:**
- Create: `src/main/java/example/web/securehome/protocol/DeviceCommand.java`

- [ ] **Step 1: Add tests for DeviceCommand**

Append to `src/test/java/example/web/securehome/protocol/DeviceCommandTest.java`:

```java
    @Test
    void deviceCommand_buildsWithRequiredFields() {
        DeviceCommand cmd = DeviceCommand.builder()
            .deviceId(7L)
            .deviceName("lock-001")
            .type(CommandType.STATE_CHANGE)
            .action("LOCKED")
            .occurredAt(java.time.Instant.now())
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
            .occurredAt(java.time.Instant.now())
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
            .payload(java.util.Map.of("battery", 12, "unit", "percent"))
            .occurredAt(java.time.Instant.now())
            .build();

        assertThat(cmd.getPayload()).containsEntry("battery", 12);
    }
```

- [ ] **Step 2: Run to verify it fails**

```bash
./gradlew test --tests "example.web.securehome.protocol.DeviceCommandTest" 2>&1 | tail -10
```

Expected: compilation error — `DeviceCommand` does not exist yet.

- [ ] **Step 3: Create `DeviceCommand`**

Create `src/main/java/example/web/securehome/protocol/DeviceCommand.java`:

```java
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
     * Examples: {"battery": 87}, {"temperature": 22.4}, {"signalStrength": -68}.
     * Defaults to empty map when not provided.
     */
    @Builder.Default
    private final Map<String, Object> payload = Collections.emptyMap();

    /** When the event occurred on the device side. */
    private final Instant occurredAt;
}
```

- [ ] **Step 4: Run to verify it passes**

```bash
./gradlew test --tests "example.web.securehome.protocol.DeviceCommandTest" 2>&1 | tail -15
```

Expected:
```
DeviceCommandTest > deviceCommand_buildsWithRequiredFields() PASSED
DeviceCommandTest > deviceCommand_payloadDefaultsToEmptyMap() PASSED
DeviceCommandTest > deviceCommand_payloadCanCarryArbitraryData() PASSED
```

- [ ] **Step 5: Commit**

```bash
git add src/main/java/example/web/securehome/protocol/DeviceCommand.java \
        src/test/java/example/web/securehome/protocol/DeviceCommandTest.java
git commit -m "feat: add DeviceCommand normalized internal language"
```

---

## Task 6: `ProtocolAdapter` interface + contract test

**Files:**
- Create: `src/main/java/example/web/securehome/protocol/ProtocolAdapter.java`
- Create: `src/test/java/example/web/securehome/protocol/ProtocolAdapterContractTest.java`

- [ ] **Step 1: Write the contract test with a minimal fake adapter**

Create `src/test/java/example/web/securehome/protocol/ProtocolAdapterContractTest.java`:

```java
package example.web.securehome.protocol;

import example.web.securehome.enums.CommunicationProtocol;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ProtocolAdapterContractTest {

    /**
     * Minimal fake adapter used only in this test.
     * Verifies that any class implementing ProtocolAdapter can produce a valid DeviceCommand.
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
```

- [ ] **Step 2: Run to verify it fails**

```bash
./gradlew test --tests "example.web.securehome.protocol.ProtocolAdapterContractTest" 2>&1 | tail -10
```

Expected: compilation error — `ProtocolAdapter` does not exist yet.

- [ ] **Step 3: Create `ProtocolAdapter`**

Create `src/main/java/example/web/securehome/protocol/ProtocolAdapter.java`:

```java
package example.web.securehome.protocol;

import example.web.securehome.enums.CommunicationProtocol;

public interface ProtocolAdapter {

    /**
     * Translate a raw protocol message into the normalized internal language.
     * The adapter is responsible for parsing the payload bytes and mapping
     * protocol-specific fields to DeviceCommand fields.
     *
     * @param raw the incoming message from any protocol
     * @return a normalized DeviceCommand ready for routing to services
     */
    DeviceCommand normalize(RawMessage raw);

    /**
     * Declares which communication protocol this adapter handles.
     * Used by the adapter registry to route incoming messages to the correct adapter.
     */
    CommunicationProtocol supports();
}
```

- [ ] **Step 4: Run all protocol tests to verify everything passes**

```bash
./gradlew test --tests "example.web.securehome.protocol.*" 2>&1 | tail -20
```

Expected:
```
DeviceCommandTest > commandType_hasThreeValues() PASSED
DeviceCommandTest > commandType_containsExpectedValues() PASSED
DeviceCommandTest > rawMessage_buildsWithAllFields() PASSED
DeviceCommandTest > rawMessage_headersDefaultToEmptyMap() PASSED
DeviceCommandTest > deviceCommand_buildsWithRequiredFields() PASSED
DeviceCommandTest > deviceCommand_payloadDefaultsToEmptyMap() PASSED
DeviceCommandTest > deviceCommand_payloadCanCarryArbitraryData() PASSED
ProtocolAdapterContractTest > adapter_normalizeReturnsDeviceCommand() PASSED
ProtocolAdapterContractTest > adapter_supportsReturnsProtocol() PASSED
ProtocolAdapterContractTest > adapter_occurredAtPropagatedFromRawMessage() PASSED
```

- [ ] **Step 5: Commit**

```bash
git add src/main/java/example/web/securehome/protocol/ProtocolAdapter.java \
        src/test/java/example/web/securehome/protocol/ProtocolAdapterContractTest.java
git commit -m "feat: add ProtocolAdapter interface and contract test"
```

---

## Self-Review

**Spec coverage:**
- ✅ Docker Compose + Mosquitto config → Task 1
- ✅ Paho dependency → Task 2
- ✅ `CommandType` → Task 3
- ✅ `RawMessage` → Task 4
- ✅ `DeviceCommand` → Task 5
- ✅ `ProtocolAdapter` interface → Task 6
- ✅ Tests for all classes → Tasks 3–6

**Placeholder scan:** No TBDs, no "add appropriate X", all code blocks are complete.

**Type consistency:**
- `RawMessage.headers` → `Map<String, String>`, uses Lombok `@Singular("header")` → test calls `.header("qos","1")` ✅
- `DeviceCommand.payload` → `Map<String, Object>`, `@Builder.Default = emptyMap()` → test asserts `.isEmpty()` ✅
- `ProtocolAdapter.normalize(RawMessage)` → `DeviceCommand` — FakeAdapter uses same types ✅
- `CommunicationProtocol` imported from existing enum in both interface and test ✅
