package ru.ssau.srestapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ssau.srestapp.entity.EventCategory;

import java.util.Optional;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {

    boolean existsByEventCategoryName(String eventCategoryName);

    Optional<EventCategory> findByEventCategoryName(String eventCategoryName);
}
