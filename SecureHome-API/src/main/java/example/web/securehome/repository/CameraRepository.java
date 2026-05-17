package example.web.securehome.repository;

import example.web.securehome.entity.Camera;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CameraRepository extends JpaRepository<Camera, Long> {
    List<Camera> findAllByRoomId(Long roomId);

    List<Camera> findAllByHomeId(Long homeId);
}
