package example.web.securehome.repository;

import example.web.securehome.entity.SmartLock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SmartLockRepository extends JpaRepository<SmartLock, Long> {
    List<SmartLock> findAllByRoomId(Long roomId);

    List<SmartLock> findAllByHomeId(Long homeId);
}
