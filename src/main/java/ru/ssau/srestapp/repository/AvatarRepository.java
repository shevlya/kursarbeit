package ru.ssau.srestapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ssau.srestapp.entity.Avatar;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {
}
