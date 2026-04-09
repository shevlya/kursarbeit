package ru.ssau.srestapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ssau.srestapp.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {
}
