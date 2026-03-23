package example.web.securehome.repository;

import example.web.securehome.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("select r from Room r where r.home.id = ?1")
    List<Room> findAllByHomeId(Long homeId);
}
