package example.web.securehome.event;

import lombok.Getter;

@Getter
public class RoomEvent extends BaseAppEvent {

    public enum Action { CREATED, UPDATED, DELETED }

    private final Long homeId;
    private final Long roomId;
    private final String roomName;
    private final Action action;

    public RoomEvent(String actor, Long homeId, Long roomId, String roomName, Action action) {
        super(actor);
        this.homeId = homeId;
        this.roomId = roomId;
        this.roomName = roomName;
        this.action = action;
    }

    @Override
    public String getCategory() {
        return "ROOM";
    }

    @Override
    public String getActionName() {
        return action.name();
    }

    @Override
    public String describe() {
        return getActor() + " " + action.name().toLowerCase() + " room " + roomName;
    }
}
