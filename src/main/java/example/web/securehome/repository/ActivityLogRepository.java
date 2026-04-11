package example.web.securehome.repository;

import example.web.securehome.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findAllByHomeIdOrderByOccurredAtDesc(Long homeId);
}
