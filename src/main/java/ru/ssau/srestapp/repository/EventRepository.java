package ru.ssau.srestapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ssau.srestapp.entity.Event;
import ru.ssau.srestapp.entity.EventStatus;
import ru.ssau.srestapp.entity.ModerationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByOrganizer_IdUser(Long organizerId);

    List<Event> findByVerifiedTrue();

    List<Event> findByEventCategory_IdEventCategory(Long categoryId);

    List<Event> findByEventStatus(EventStatus status);

    @Query("SELECT e FROM Event e WHERE e.eventDate BETWEEN :from AND :to AND e.verified = true")
    List<Event> findByDateRangeAndVerified(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT e FROM Event e WHERE e.eventDate >= :today AND e.verified = true ORDER BY e.eventDate ASC")
    List<Event> findActiveAndVerified(@Param("today") LocalDateTime today);

    @Query("SELECT e FROM Event e WHERE LOWER(e.eventName) LIKE LOWER(CONCAT('%', :keyword, '%')) AND e.verified = true")
    List<Event> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT e FROM Event e WHERE e.eventStatus = :status AND e.startTime <= :now")
    List<Event> findEventsByStatusAndStartTimeBefore(@Param("status") EventStatus status, @Param("now") LocalDateTime now);

    @Query("SELECT e FROM Event e WHERE e.eventStatus = :status AND e.endTime <= :now")
    List<Event> findEventsByStatusAndEndTimeBefore(@Param("status") EventStatus status, @Param("now") LocalDateTime now);

    long countByVerifiedFalse();

    List<Event> findByModerationStatus(ModerationStatus status);

    @Query("SELECT e FROM Event e " +
            "JOIN FETCH e.organizer " +
            "JOIN FETCH e.eventFormat " +
            "JOIN FETCH e.eventCategory " +
            "JOIN FETCH e.eventStatus " +
            "LEFT JOIN FETCH e.place " +
            "LEFT JOIN FETCH e.admin " +
            "WHERE e.idEvent = :id")
    Optional<Event> findByIdWithDetails(@Param("id") Long id);
}
