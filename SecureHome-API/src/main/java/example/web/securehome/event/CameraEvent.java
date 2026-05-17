package example.web.securehome.event;

public class CameraEvent extends DeviceEvent<CameraEvent.Action> {

    public enum Action implements DeviceAction { ARMED, DISARMED }

    public CameraEvent(String actorEmail, Long deviceId, String deviceName, Long homeId, Action action) {
        super(actorEmail, deviceId, deviceName, homeId, action);
    }

    @Override
    public String getCategory() {
        return "CAMERA";
    }

    @Override
    public String describe() {
        return getActor() + " " + getAction().name().toLowerCase() + " camera " + getDeviceName();
    }
}
