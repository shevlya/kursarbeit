package ru.ssau.srestapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ssau.srestapp.entity.EventParticipant;
import ru.ssau.srestapp.entity.EventParticipantId;
import ru.ssau.srestapp.entity.ParticipationStatus;

import java.util.List;
import java.util.Optional;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, EventParticipantId> {

    List<EventParticipant> findByIdEvent_IdEvent(Long eventId);

    List<EventParticipant> findByIdUser_IdUser(Long userId);

    Optional<EventParticipant> findByIdUser_IdUserAndIdEvent_IdEvent(Long userId, Long eventId);

    @Query("SELECT COUNT(ep) FROM EventParticipant ep WHERE ep.idEvent.idEvent = :eventId AND ep.participationStatus = :status")
    long countByParticipationStatus(@Param("eventId") Long eventId, @Param("status") ParticipationStatus status);

    @Query("SELECT ep FROM EventParticipant ep WHERE ep.idEvent.idEvent = :eventId AND ep.participationStatus = :status ORDER BY ep.registrationDate ASC")
    List<EventParticipant> findByParticipationStatus(@Param("eventId") Long eventId, @Param("status") ParticipationStatus status);

    @Query("SELECT ep FROM EventParticipant ep WHERE ep.idEvent.idEvent = :eventId AND ep.participationStatus IN :statuses")
    List<EventParticipant> findByParticipationStatusIn(@Param("eventId") Long eventId, @Param("statuses") List<ParticipationStatus> statuses);

    boolean existsByIdUser_IdUserAndIdEvent_IdEventAndParticipationStatusIn(Long userId, Long eventId, List<ParticipationStatus> statuses);
}
