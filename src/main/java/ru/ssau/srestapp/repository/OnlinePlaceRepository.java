package ru.ssau.srestapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ssau.srestapp.entity.OnlinePlace;

public interface OnlinePlaceRepository extends JpaRepository<OnlinePlace, Long> {
}
