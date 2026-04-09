package ru.ssau.srestapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ssau.srestapp.entity.PhysicalPlace;

public interface PhysicalPlaceRepository extends JpaRepository<PhysicalPlace, Long> {

}
