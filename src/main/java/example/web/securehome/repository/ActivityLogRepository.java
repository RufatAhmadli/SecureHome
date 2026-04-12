package example.web.securehome.repository;

import example.web.securehome.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    // TODO: This query is unbounded — returns every log for the home with no limit.
    // As logs grow this will become a performance problem.
    // Fix: switch to Page<ActivityLog> with Pageable, add date range + category filters,
    // and add a DB index on (home_id, occurred_at DESC).
    List<ActivityLog> findAllByHomeIdOrderByOccurredAtDesc(Long homeId);
}
