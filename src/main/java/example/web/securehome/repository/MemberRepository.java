package example.web.securehome.repository;

import example.web.securehome.entity.HomeMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<HomeMember, Long> {
    List<HomeMember> findAllByHomeId(Long homeId);

    List<HomeMember> findAllByUserId(Long userId);

    boolean existsHomeMemberByUserIdAndHomeId(Long userId, Long homeId);

    Optional<HomeMember> findByHomeIdAndUserId(Long homeId, Long userId);
}
