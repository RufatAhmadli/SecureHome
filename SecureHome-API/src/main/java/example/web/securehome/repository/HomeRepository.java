package example.web.securehome.repository;

import example.web.securehome.entity.Home;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HomeRepository extends JpaRepository<Home, Long> {
    @Query("select h from Home h inner join h.members members where members.user.id = ?1")
    List<Home> findAllByMembersUserId(Long userId);

    @Query("select h from Home h inner join h.members members where h.id = ?1 and members.user.id = ?2")
    Optional<Home> findByIdAndMembersUserId(Long homeId, Long userId);

    boolean existsByNameAndMembersUserId(String name, Long userId);

}
