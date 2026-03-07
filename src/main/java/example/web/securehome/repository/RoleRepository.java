package example.web.securehome.repository;

import example.web.securehome.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("select r from Role r where upper(r.roleName) like upper(concat('%', ?1, '%'))")
    Role findByRoleNameContainsIgnoreCase(String roleName);
}
