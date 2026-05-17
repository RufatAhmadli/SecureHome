package example.web.securehome.event;

import example.web.securehome.entity.ActivityLog;
import example.web.securehome.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActivityLogListener {

    private final ActivityLogRepository activityLogRepository;

    @EventListener
    public void onAnyEvent(BaseAppEvent event) {
        activityLogRepository.save(ActivityLog.builder()
                .actor(event.getActor())
                .category(event.getCategory())
                .action(event.getActionName())
                .description(event.describe())
                .homeId(event.getHomeId())
                .occurredAt(event.getOccurredAt())
                .build());
    }
}
