package example.web.securehome.event;

import lombok.Getter;

@Getter
public class HomeEvent extends BaseAppEvent {

    public enum Action { CREATED, UPDATED, DELETED }

    private final Long homeId;
    private final String homeName;
    private final Action action;

    public HomeEvent(String actor, Long homeId, String homeName, Action action) {
        super(actor);
        this.homeId = homeId;
        this.homeName = homeName;
        this.action = action;
    }

    @Override
    public String getCategory() {
        return "HOME";
    }

    @Override
    public String getActionName() {
        return action.name();
    }

    @Override
    public String describe() {
        return getActor() + " " + action.name().toLowerCase() + " home " + homeName;
    }
}
