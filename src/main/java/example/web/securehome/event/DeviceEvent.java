package example.web.securehome.event;

import lombok.Getter;

@Getter
public class DeviceEvent extends BaseAppEvent {

    public enum Action implements DeviceAction { CREATED, UPDATED, DELETED }

    private final Long deviceId;
    private final String deviceName;
    private final Long homeId;
    private final DeviceAction action;

    public DeviceEvent(String actorEmail, Long deviceId, String deviceName, Long homeId, DeviceAction action) {
        super(actorEmail);
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.homeId = homeId;
        this.action = action;
    }
}
