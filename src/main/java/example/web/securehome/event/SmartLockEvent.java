package example.web.securehome.event;

import lombok.Getter;

@Getter
public class SmartLockEvent extends DeviceEvent {

    public enum Action implements DeviceAction { LOCKED, UNLOCKED }

    public SmartLockEvent(String actorEmail, Long deviceId, String deviceName, Long homeId, Action action) {
        super(actorEmail, deviceId, deviceName, homeId, action);
    }
}
