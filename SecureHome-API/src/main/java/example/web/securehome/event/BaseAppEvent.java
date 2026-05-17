package example.web.securehome.event;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public abstract class BaseAppEvent {

    private final String actor;
    private final LocalDateTime occurredAt;

    protected BaseAppEvent(String actor) {
        this.actor = actor;
        this.occurredAt = LocalDateTime.now();
    }

    public abstract Long getHomeId();
    public abstract String getActionName();
    public abstract String getCategory();
    public abstract String describe();
}
