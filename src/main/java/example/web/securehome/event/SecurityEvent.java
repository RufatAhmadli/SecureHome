package example.web.securehome.event;

import lombok.Getter;

@Getter
public class SecurityEvent extends BaseAppEvent {

    public enum Action { UNAUTHORIZED_DEVICE_COMMAND }

    private final Long homeId;
    private final Action action;
    private final String detail;

    public SecurityEvent(Long homeId, Action action, String detail) {
        super("system");
        this.homeId = homeId;
        this.action = action;
        this.detail = detail;
    }

    @Override
    public String getCategory() {
        return "SECURITY";
    }

    @Override
    public String getActionName() {
        return action.name();
    }

    @Override
    public String describe() {
        return detail;
    }
}
