package example.web.securehome.event;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public abstract class BaseAppEvent {

    private final String actorEmail;
    private final LocalDateTime occurredAt;

    protected BaseAppEvent(String actorEmail) {
        this.actorEmail = actorEmail;
        this.occurredAt = LocalDateTime.now();
    }

    public abstract Long getHomeId();
    public abstract String getActionName();
    public abstract String getCategory();
    public abstract String describe();
}
