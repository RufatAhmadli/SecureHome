package example.web.securehome.event;

import lombok.Getter;

@Getter
public class CameraEvent extends DeviceEvent {

    public enum Action implements DeviceAction { ARMED, DISARMED }

    public CameraEvent(String actorEmail, Long deviceId, String deviceName, Long homeId, Action action) {
        super(actorEmail, deviceId, deviceName, homeId, action);
    }
}
