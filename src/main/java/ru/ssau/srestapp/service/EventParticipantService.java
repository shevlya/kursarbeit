package ru.ssau.srestapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.srestapp.dto.eventParticipant.EventParticipantResponseDto;
import ru.ssau.srestapp.dto.eventParticipant.EventParticipantShortDto;
import ru.ssau.srestapp.entity.*;
import ru.ssau.srestapp.exception.*;
import ru.ssau.srestapp.repository.EventParticipantRepository;
import ru.ssau.srestapp.repository.EventRepository;
import ru.ssau.srestapp.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.lang.Boolean.TRUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventParticipantService {

    private final EventParticipantRepository eventParticipantRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Transactional
    public EventParticipantResponseDto register(Long userId, Long eventId)
            throws EntityNotFoundException, ParticipantAlreadyExistsException {
        User user = findUserOrThrow(userId);
        Event event = findEventOrThrow(eventId);
        Optional<EventParticipant> existing = eventParticipantRepository
                .findByIdUser_IdUserAndIdEvent_IdEvent(userId, eventId);

        if (existing.isPresent()) {
            EventParticipant participant = existing.get();
            if (participant.getParticipationStatus() == ParticipationStatus.CANCELLED) {
                long registeredCount = eventParticipantRepository
                        .countByParticipationStatus(eventId, ParticipationStatus.REGISTERED);
                Integer maxParticipants = event.getMaxParticipants();
                ParticipationStatus newStatus = (maxParticipants == null || maxParticipants == 0 || registeredCount < maxParticipants)
                        ? ParticipationStatus.REGISTERED : ParticipationStatus.WAITLISTED;
                return updateParticipantStatus(participant, newStatus, user, event);
            } else {
                throw new ParticipantAlreadyExistsException(userId, eventId);
            }
        }

        long registeredCount = eventParticipantRepository
                .countByParticipationStatus(eventId, ParticipationStatus.REGISTERED);
        Integer maxParticipants = event.getMaxParticipants();
        ParticipationStatus status = (maxParticipants == null || maxParticipants == 0 || registeredCount < maxParticipants)
                ? ParticipationStatus.REGISTERED : ParticipationStatus.WAITLISTED;

        EventParticipant entity = new EventParticipant();
        entity.setIdUser(user);
        entity.setIdEvent(event);
        entity.setParticipationStatus(status);
        entity.setRegistrationDate(LocalDateTime.now());
        EventParticipantResponseDto response = toDto(eventParticipantRepository.save(entity));

        if (status == ParticipationStatus.REGISTERED) {
            emailService.sendParticipationConfirmed(
                    user.getEmail(),
                    user.getFio(),
                    event.getEventName(),
                    event.getStartTime().toString(),
                    event.getPlace() != null ? event.getPlace().getPlaceName() : "Онлайн"
            );
        }
        return response;
    }

    private EventParticipantResponseDto updateParticipantStatus(EventParticipant participant,
                                                                ParticipationStatus newStatus,
                                                                User user, Event event) {
        participant.setParticipationStatus(newStatus);
        participant.setRegistrationDate(LocalDateTime.now());
        EventParticipantResponseDto response = toDto(eventParticipantRepository.save(participant));
        if (newStatus == ParticipationStatus.REGISTERED) {
            emailService.sendParticipationConfirmed(
                    user.getEmail(),
                    user.getFio(),
                    event.getEventName(),
                    event.getStartTime().toString(),
                    event.getPlace() != null ? event.getPlace().getPlaceName() : "Онлайн"
            );
        }
        return response;
    }

    @Transactional
    public EventParticipantResponseDto cancelParticipation(Long userId, Long eventId, Boolean sendEmail)
            throws EntityNotFoundException, ParticipantNotFoundException {
        EventParticipant participant = findParticipantOrThrow(userId, eventId);
        if (participant.getParticipationStatus() == ParticipationStatus.REJECTED_BY_ORGANIZER) {
            throw new IllegalArgumentException("Нельзя отменить участие, если вы отклонены организатором");
        }
        participant.setParticipationStatus(ParticipationStatus.CANCELLED);
        EventParticipantResponseDto response = toDto(eventParticipantRepository.save(participant));
        if (TRUE.equals(sendEmail)) {
            emailService.sendParticipationCancelled(
                    participant.getIdUser().getEmail(),
                    participant.getIdUser().getFio(),
                    participant.getIdEvent().getEventName(),
                    participant.getIdEvent().getStartTime().toString()
            );
        }
        promoteFromWaitlist(eventId, sendEmail);
        return response;
    }

    @Transactional
    public EventParticipantResponseDto removeParticipant(Long userId, Long eventId, Boolean sendEmail)
            throws EntityNotFoundException, ParticipantNotFoundException {
        EventParticipant participant = findParticipantOrThrow(userId, eventId);
        participant.setParticipationStatus(ParticipationStatus.REJECTED_BY_ORGANIZER);
        EventParticipantResponseDto response = toDto(eventParticipantRepository.save(participant));
        if (TRUE.equals(sendEmail)) {
            emailService.sendParticipationRejected(
                    participant.getIdUser().getEmail(),
                    participant.getIdUser().getFio(),
                    participant.getIdEvent().getEventName(),
                    "Организатор отклонил вашу заявку"
            );
        }
        promoteFromWaitlist(eventId, sendEmail);
        return response;
    }

    @Transactional
    public EventParticipantResponseDto changeStatus(Long userId, Long eventId,
                                                    ParticipationStatus newStatus, Boolean sendEmail)
            throws EntityNotFoundException, ParticipantNotFoundException {
        EventParticipant participant = findParticipantOrThrow(userId, eventId);
        participant.setParticipationStatus(newStatus);
        EventParticipantResponseDto response = toDto(eventParticipantRepository.save(participant));
        if (TRUE.equals(sendEmail)) {
            if (newStatus == ParticipationStatus.ATTENDED) {
                emailService.sendParticipationAttended(
                        participant.getIdUser().getEmail(),
                        participant.getIdUser().getFio(),
                        participant.getIdEvent().getEventName(),
                        participant.getIdEvent().getStartTime().toString()
                );
            } else if (newStatus == ParticipationStatus.CANCELLED) {
                emailService.sendParticipationCancelled(
                        participant.getIdUser().getEmail(),
                        participant.getIdUser().getFio(),
                        participant.getIdEvent().getEventName(),
                        participant.getIdEvent().getStartTime().toString()
                );
            } else if (newStatus == ParticipationStatus.REJECTED_BY_ORGANIZER) {
                emailService.sendParticipationRejected(
                        participant.getIdUser().getEmail(),
                        participant.getIdUser().getFio(),
                        participant.getIdEvent().getEventName(),
                        "Организатор изменил статус участия"
                );
            }
        }
        return response;
    }

    @Transactional(readOnly = true)
    public List<EventParticipantShortDto> getParticipantsByEvent(Long eventId) {
        return eventParticipantRepository.findByIdEvent_IdEvent(eventId)
                .stream().map(this::toShortDto).toList();
    }

    @Transactional(readOnly = true)
    public List<EventParticipantShortDto> getRegisteredParticipants(Long eventId) {
        return eventParticipantRepository.findByIdEvent_IdEvent(eventId)
                .stream()
                .filter(ep -> ep.getParticipationStatus() == ParticipationStatus.REGISTERED)
                .map(this::toShortDto).toList();
    }

    @Transactional(readOnly = true)
    public List<EventParticipantShortDto> getWaitlistedParticipants(Long eventId) {
        return eventParticipantRepository
                .findByParticipationStatus(eventId, ParticipationStatus.WAITLISTED)
                .stream().map(this::toShortDto).toList();
    }

    @Transactional(readOnly = true)
    public List<EventParticipantShortDto> getEventsByUser(Long userId) {
        return eventParticipantRepository.findByIdUser_IdUser(userId)
                .stream().map(this::toShortDto).toList();
    }

    @Transactional(readOnly = true)
    public EventParticipantResponseDto getParticipant(Long userId, Long eventId)
            throws ParticipantNotFoundException {
        return toDto(findParticipantOrThrow(userId, eventId));
    }

    @Transactional
    public void deleteParticipant(Long userId, Long eventId) throws ParticipantNotFoundException {
        findParticipantOrThrow(userId, eventId);
        eventParticipantRepository.deleteById(new EventParticipantId(userId, eventId));
    }

    @Transactional
    public void promoteFromWaitlist(Long eventId, Boolean sendEmail) throws EntityNotFoundException {
        long registeredCount = eventParticipantRepository
                .countByParticipationStatus(eventId, ParticipationStatus.REGISTERED);
        Event event = findEventOrThrow(eventId);
        Integer maxParticipants = event.getMaxParticipants();
        if (maxParticipants == null || maxParticipants == 0 || registeredCount >= maxParticipants) {
            log.info("Нет свободных мест для продвижения из листа ожидания (eventId={})", eventId);
            return;
        }
        List<EventParticipant> waitlisted = eventParticipantRepository
                .findByParticipationStatus(eventId, ParticipationStatus.WAITLISTED);
        if (!waitlisted.isEmpty()) {
            EventParticipant firstInLine = waitlisted.get(0);
            log.info("Продвижение пользователя {} из листа ожидания (eventId={})",
                    firstInLine.getIdUser().getIdUser(), eventId);
            firstInLine.setParticipationStatus(ParticipationStatus.REGISTERED);
            eventParticipantRepository.save(firstInLine);
            if (TRUE.equals(sendEmail)) {
                emailService.sendWaitlistPromoted(
                        firstInLine.getIdUser().getEmail(),
                        firstInLine.getIdUser().getFio(),
                        firstInLine.getIdEvent().getEventName(),
                        firstInLine.getIdEvent().getStartTime().toString()
                );
            }
        }
    }

    private User findUserOrThrow(Long id) throws EntityNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(EntityType.USER.notFound(id)));
    }

    private Event findEventOrThrow(Long id) throws EntityNotFoundException {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(EntityType.EVENT.notFoundNeuter(id)));
    }

    private EventParticipant findParticipantOrThrow(Long userId, Long eventId)
            throws ParticipantNotFoundException {
        return eventParticipantRepository.findByIdUser_IdUserAndIdEvent_IdEvent(userId, eventId)
                .orElseThrow(() -> new ParticipantNotFoundException(userId, eventId));
    }

    private EventParticipantResponseDto toDto(EventParticipant ep) {
        return new EventParticipantResponseDto(
                ep.getIdUser().getIdUser(),
                ep.getIdUser().getFio(),
                ep.getIdUser().getEmail(),
                ep.getIdEvent().getIdEvent(),
                ep.getIdEvent().getEventName(),
                ep.getParticipationStatus(),
                ep.getRegistrationDate()
        );
    }

    private EventParticipantShortDto toShortDto(EventParticipant ep) {
        return new EventParticipantShortDto(
                ep.getIdUser().getIdUser(),
                ep.getIdUser().getFio(),
                ep.getIdEvent().getIdEvent(),
                ep.getIdEvent().getEventName(),
                ep.getParticipationStatus(),
                ep.getRegistrationDate()
        );
    }
}
