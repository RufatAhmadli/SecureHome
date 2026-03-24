package example.web.securehome.repository;

import example.web.securehome.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findAllByRoomId(Long roomId);
    List<Device> findAllByHomeId(Long homeId);
}
