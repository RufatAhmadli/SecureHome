package example.web.securehome.event;

import lombok.Getter;

@Getter
public class MemberEvent extends BaseAppEvent {

    public enum Action { ADDED, REMOVED, ROLE_CHANGED }

    private final Long homeId;
    private final String targetEmail;
    private final Action action;

    public MemberEvent(String actorEmail, Long homeId, String targetEmail, Action action) {
        super(actorEmail);
        this.homeId = homeId;
        this.targetEmail = targetEmail;
        this.action = action;
    }

    @Override
    public String getCategory() {
        return "MEMBER";
    }

    @Override
    public String getActionName() {
        return action.name();
    }

    @Override
    public String describe() {
        return getActorEmail() + " " + action.name().toLowerCase().replace("_", " ") + " member " + targetEmail;
    }
}
