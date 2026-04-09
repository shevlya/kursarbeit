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

@Service
@RequiredArgsConstructor
@Slf4j
public class EventParticipantService {

    private static final String ONLINE_PLACE_NAME = "Онлайн";

    private final EventParticipantRepository eventParticipantRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    private record EmailData(String email, String fio, String eventName, String eventTime) {
    }

    @Transactional
    public EventParticipantResponseDto register(Long userId, Long eventId) throws EntityNotFoundException, ParticipantAlreadyExistsException {
        User user = findUserOrThrow(userId);
        Event event = findEventOrThrow(eventId);
        Optional<EventParticipant> existing = eventParticipantRepository.findByIdUser_IdUserAndIdEvent_IdEvent(userId, eventId);
        if (existing.isPresent()) {
            EventParticipant participant = existing.get();
            if (participant.getParticipationStatus() == ParticipationStatus.CANCELLED) {
                ParticipationStatus newStatus = determineParticipationStatus(eventId, event);
                return updateParticipantStatus(participant, newStatus, user, event);
            } else {
                throw new ParticipantAlreadyExistsException(userId, eventId);
            }
        }
        ParticipationStatus status = determineParticipationStatus(eventId, event);
        EventParticipant entity = new EventParticipant();
        entity.setIdUser(user);
        entity.setIdEvent(event);
        entity.setParticipationStatus(status);
        entity.setRegistrationDate(LocalDateTime.now());
        EventParticipantResponseDto response = toDto(eventParticipantRepository.save(entity));
        if (status == ParticipationStatus.REGISTERED) {
            sendConfirmationEmail(user, event);
        }
        return response;
    }

    private EventParticipantResponseDto updateParticipantStatus(EventParticipant participant, ParticipationStatus newStatus, User user, Event event) {
        participant.setParticipationStatus(newStatus);
        participant.setRegistrationDate(LocalDateTime.now());
        EventParticipantResponseDto response = toDto(eventParticipantRepository.save(participant));
        if (newStatus == ParticipationStatus.REGISTERED) {
            sendConfirmationEmail(user, event);
        }
        return response;
    }

    private void sendConfirmationEmail(User user, Event event) {
        emailService.sendParticipationConfirmed(
                user.getEmail(),
                user.getFio(),
                event.getEventName(),
                event.getStartTime().toString(),
                getPlaceName(event)
        );
    }

    private String getPlaceName(Event event) {
        return event.getPlace() != null ? event.getPlace().getPlaceName() : ONLINE_PLACE_NAME;
    }

    private EmailData extractEmailData(EventParticipant participant) {
        return new EmailData(
                participant.getIdUser().getEmail(),
                participant.getIdUser().getFio(),
                participant.getIdEvent().getEventName(),
                participant.getIdEvent().getStartTime().toString()
        );
    }

    private void sendStatusEmail(ParticipationStatus status, EmailData data, String customComment) {
        switch (status) {
            case ATTENDED -> emailService.sendParticipationAttended(
                    data.email(), data.fio(), data.eventName(), data.eventTime());
            case CANCELLED -> emailService.sendParticipationCancelled(
                    data.email(), data.fio(), data.eventName(), data.eventTime());
            case REJECTED_BY_ORGANIZER -> emailService.sendParticipationRejected(
                    data.email(), data.fio(), data.eventName(),
                    customComment != null ? customComment : "Организатор изменил статус участия");
            case WAITLISTED -> emailService.sendWaitlistPromoted(
                    data.email(), data.fio(), data.eventName(), data.eventTime());
        }
    }

    @Transactional
    public EventParticipantResponseDto cancelParticipation(Long userId, Long eventId, Boolean sendEmail) throws EntityNotFoundException, ParticipantNotFoundException {
        EventParticipant participant = findParticipantOrThrow(userId, eventId);
        if (participant.getParticipationStatus() == ParticipationStatus.REJECTED_BY_ORGANIZER) {
            throw new IllegalArgumentException("Нельзя отменить участие, если вы отклонены организатором");
        }
        participant.setParticipationStatus(ParticipationStatus.CANCELLED);
        EventParticipantResponseDto response = toDto(eventParticipantRepository.save(participant));
        if (sendEmail != null && sendEmail) {
            sendStatusEmail(ParticipationStatus.CANCELLED, extractEmailData(participant), null);
        }
        promoteFromWaitlist(eventId, sendEmail);
        return response;
    }

    @Transactional
    public EventParticipantResponseDto removeParticipant(Long userId, Long eventId, Boolean sendEmail) throws EntityNotFoundException, ParticipantNotFoundException {
        EventParticipant participant = findParticipantOrThrow(userId, eventId);
        participant.setParticipationStatus(ParticipationStatus.REJECTED_BY_ORGANIZER);
        EventParticipantResponseDto response = toDto(eventParticipantRepository.save(participant));
        if (sendEmail != null && sendEmail) {
            sendStatusEmail(ParticipationStatus.REJECTED_BY_ORGANIZER, extractEmailData(participant), "Организатор отклонил вашу заявку");
        }
        promoteFromWaitlist(eventId, sendEmail);
        return response;
    }

    @Transactional
    public EventParticipantResponseDto changeStatus(Long userId, Long eventId, ParticipationStatus newStatus, Boolean sendEmail) throws ParticipantNotFoundException {
        EventParticipant participant = findParticipantOrThrow(userId, eventId);
        participant.setParticipationStatus(newStatus);
        EventParticipantResponseDto response = toDto(eventParticipantRepository.save(participant));
        if (sendEmail != null && sendEmail) {
            sendStatusEmail(newStatus, extractEmailData(participant), null);
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
    public EventParticipantResponseDto getParticipant(Long userId, Long eventId) throws ParticipantNotFoundException {
        return toDto(findParticipantOrThrow(userId, eventId));
    }

    @Transactional
    public void deleteParticipant(Long userId, Long eventId) throws ParticipantNotFoundException {
        findParticipantOrThrow(userId, eventId);
        eventParticipantRepository.deleteById(new EventParticipantId(userId, eventId));
    }

    @Transactional
    public void promoteFromWaitlist(Long eventId, Boolean sendEmail) throws EntityNotFoundException {
        ParticipationStatus registeredStatus = ParticipationStatus.REGISTERED;
        long registeredCount = eventParticipantRepository
                .countByIdEvent_IdEventAndParticipationStatus(eventId, registeredStatus);
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
            log.info("Продвижение пользователя {} из листа ожидания (eventId={})", firstInLine.getIdUser().getIdUser(), eventId);
            firstInLine.setParticipationStatus(ParticipationStatus.REGISTERED);
            eventParticipantRepository.save(firstInLine);
            if (sendEmail != null && sendEmail) {
                sendStatusEmail(ParticipationStatus.WAITLISTED, extractEmailData(firstInLine), null);
            }
        }
    }

    private User findUserOrThrow(Long id) throws EntityNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(EntityType.USER.notFound(id)));
    }

    private Event findEventOrThrow(Long id) throws EntityNotFoundException {
        return eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(EntityType.EVENT.notFoundNeuter(id)));
    }

    private EventParticipant findParticipantOrThrow(Long userId, Long eventId) throws ParticipantNotFoundException {
        return eventParticipantRepository.findByIdUser_IdUserAndIdEvent_IdEvent(userId, eventId).orElseThrow(() -> new ParticipantNotFoundException(userId, eventId));
    }

    private ParticipationStatus determineParticipationStatus(Long eventId, Event event) {
        long registeredCount = eventParticipantRepository
                .countByIdEvent_IdEventAndParticipationStatus(eventId, ParticipationStatus.REGISTERED);
        Integer maxParticipants = event.getMaxParticipants();
        return (maxParticipants == null || maxParticipants == 0 || registeredCount < maxParticipants)
                ? ParticipationStatus.REGISTERED
                : ParticipationStatus.WAITLISTED;
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
