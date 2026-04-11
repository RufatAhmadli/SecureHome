package example.web.securehome.protocol;

public enum CommandType {
    /** Device state changed: LOCKED, ARMED, ONLINE, OFFLINE, etc. */
    STATE_CHANGE,

    /** Sensor reading reported: battery level, temperature, signal strength. */
    TELEMETRY,

    /** Device alive ping — presence confirmation, no state change. */
    HEARTBEAT
}
