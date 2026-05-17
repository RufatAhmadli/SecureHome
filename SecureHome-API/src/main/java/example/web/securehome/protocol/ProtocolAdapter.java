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
