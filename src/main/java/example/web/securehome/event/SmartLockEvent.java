package example.web.securehome.event;

public class SmartLockEvent extends DeviceEvent<SmartLockEvent.Action> {

    public enum Action implements DeviceAction { LOCKED, UNLOCKED }

    public SmartLockEvent(String actorEmail, Long deviceId, String deviceName, Long homeId, Action action) {
        super(actorEmail, deviceId, deviceName, homeId, action);
    }

    @Override
    public String getCategory() {
        return "SMART_LOCK";
    }

    @Override
    public String describe() {
        return getActor() + " " + getAction().name().toLowerCase() + " device " + getDeviceName();
    }
}
