package example.web.securehome.event;

import lombok.Getter;

@Getter
public class DeviceEvent<A extends Enum<A> & DeviceAction> extends BaseAppEvent {

    public enum Action implements DeviceAction { CREATED, UPDATED, DELETED }

    private final Long deviceId;
    private final String deviceName;
    private final Long homeId;
    private final A action;

    public DeviceEvent(String actorEmail, Long deviceId, String deviceName, Long homeId, A action) {
        super(actorEmail);
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.homeId = homeId;
        this.action = action;
    }

    @Override
    public String getCategory() {
        return "DEVICE";
    }

    @Override
    public String getActionName() {
        return action.name();
    }

    @Override
    public String describe() {
        return getActorEmail() + " performed " + action.name() + " on device " + deviceName;
    }
}
