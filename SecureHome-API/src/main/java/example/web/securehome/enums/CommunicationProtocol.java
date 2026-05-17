package example.web.securehome.enums;

public enum CommunicationProtocol {
    MQTT,    // device publishes via MQTT broker (current impl, over WiFi/Ethernet)
    MATTER,  // Matter fabric — local control standard (future)
    HTTP     // (future)
}
