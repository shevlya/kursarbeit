package ru.ssau.srestapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ssau.srestapp.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    boolean existsByRoleName(String roleName);

    Optional<Role> findByRoleName(String roleName);
}
