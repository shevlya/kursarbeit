package ru.ssau.srestapp.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.srestapp.dto.event.*;
import ru.ssau.srestapp.entity.*;
import ru.ssau.srestapp.exception.*;
import ru.ssau.srestapp.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final EventCategoryRepository eventCategoryRepository;
    private final PlaceRepository placeRepository;

    @Transactional(readOnly = true)
    public List<EventShortDto> getAll() {
        return eventRepository.findAll().stream().map(this::toShortDto).toList();
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getAllVerified() {
        return eventRepository.findByVerifiedTrue().stream().map(this::toShortDto).toList();
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getActiveAndVerified() {
        LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();
        return eventRepository.findActiveAndVerified(today).stream().map(this::toShortDto).toList();
    }

    @Transactional(readOnly = true)
    public EventResponseDto getById(Long id) throws EntityNotFoundException {
        Event event = eventRepository.findByIdWithDetails(id).orElseThrow(() -> new EntityNotFoundException(EntityType.EVENT.notFoundNeuter(id)));
        return toDto(event);
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getByOrganizer(Long organizerId) {
        return eventRepository.findByOrganizer_IdUser(organizerId).stream().map(this::toShortDto).toList();
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getByCategory(Long categoryId) {
        return eventRepository.findByEventCategory_IdEventCategory(categoryId).stream().map(this::toShortDto).toList();
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> search(String keyword) {
        return eventRepository.searchByKeyword(keyword).stream().map(this::toShortDto).toList();
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getByStatus(EventStatus status) {
        return eventRepository.findByEventStatus(status).stream().map(this::toShortDto).toList();
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getByDateRange(LocalDateTime from, LocalDateTime to) {
        return eventRepository.findByDateRangeAndVerified(from, to).stream().map(this::toShortDto).toList();
    }

    @Transactional
    public EventResponseDto create(EventRequestDto dto) throws EntityNotFoundException, InvalidDateTimeException {
        validateDateTime(dto.getStartTime(), dto.getEndTime());
        Event entity = new Event();
        updateEntityFromDto(entity, dto);
        return toDto(eventRepository.save(entity));
    }

    @Transactional
    public EventResponseDto submitChanges(Long eventId, @Valid EventSubmitChangesDto dto) throws EntityNotFoundException, EventNotEditableException {
        Event entity = findOrThrow(eventId);
        if (entity.getEventStatus() == EventStatus.ONGOING || entity.getEventStatus() == EventStatus.COMPLETED) {
            throw new EventNotEditableException();
        }
        Map<String, Object> changes = new HashMap<>();
        if (dto.getEventName() != null && !dto.getEventName().equals(entity.getEventName()))
            changes.put("eventName", dto.getEventName());
        if (dto.getEventDescription() != null && !dto.getEventDescription().equals(entity.getEventDescription()))
            changes.put("eventDescription", dto.getEventDescription());
        if (dto.getEventDate() != null && !dto.getEventDate().equals(entity.getEventDate()))
            changes.put("eventDate", dto.getEventDate().toString());
        if (dto.getStartTime() != null && !dto.getStartTime().equals(entity.getStartTime()))
            changes.put("startTime", dto.getStartTime().toString());
        if (dto.getEndTime() != null && !dto.getEndTime().equals(entity.getEndTime()))
            changes.put("endTime", dto.getEndTime().toString());
        if (dto.getMaxParticipants() != null && !dto.getMaxParticipants().equals(entity.getMaxParticipants()))
            changes.put("maxParticipants", dto.getMaxParticipants());
        if (dto.getImageUrl() != null && !dto.getImageUrl().equals(entity.getImageUrl()))
            changes.put("imageUrl", dto.getImageUrl());
        if (dto.getPrice() != null && dto.getPrice().compareTo(entity.getPrice()) != 0)
            changes.put("price", dto.getPrice());
        if (dto.getEventFormat() != null && dto.getEventFormat() != entity.getEventFormat())
            changes.put("eventFormat", dto.getEventFormat());
        if (dto.getIdEventCategory() != null && !dto.getIdEventCategory().equals(entity.getEventCategory().getIdEventCategory()))
            changes.put("eventCategoryId", dto.getIdEventCategory());
        if (dto.getIdPlace() != null && (entity.getPlace() == null || !dto.getIdPlace().equals(entity.getPlace().getIdPlace())))
            changes.put("placeId", dto.getIdPlace());
        if (changes.isEmpty()) {
            return toDto(entity);
        }
        entity.setDraftChanges(changes);
        entity.setModerationStatus(ModerationStatus.PENDING);
        eventRepository.save(entity);
        return toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getPendingEvents() {
        return eventRepository.findByModerationStatus(ModerationStatus.PENDING)
                .stream().map(this::toShortDto).toList();
    }

    @Transactional
    public EventResponseDto approveChanges(Long eventId, ApproveChangesDto approveDto)
            throws EntityNotFoundException, InvalidDateTimeException, ModerationException {
        Event entity = findOrThrow(eventId);
        if (entity.getModerationStatus() != ModerationStatus.PENDING) {
            throw new ModerationException();
        }
        Map<String, Object> changes = entity.getDraftChanges();
        if (changes == null || changes.isEmpty()) {
            entity.setModerationStatus(ModerationStatus.PUBLISHED);
            return toDto(eventRepository.save(entity));
        }
        boolean applyAll = approveDto.getApplyAll() != null && approveDto.getApplyAll();
        List<String> fieldsToApply = approveDto.getFields();
        List<String> appliedFieldsRussian = new ArrayList<>();
        for (Map.Entry<String, Object> entry : changes.entrySet()) {
            String field = entry.getKey();
            Object newValue = entry.getValue();
            if (applyAll || (fieldsToApply != null && fieldsToApply.contains(field))) {
                applyFieldChange(entity, field, newValue);
                appliedFieldsRussian.add(mapFieldToRussian(field));
            }
        }
        entity.setDraftChanges(null);
        entity.setModerationStatus(ModerationStatus.PUBLISHED);
        eventRepository.save(entity);
        emailService.sendEventChangesApproved(
                entity.getOrganizer().getEmail(),
                entity.getEventName(),
                appliedFieldsRussian,
                "Администратор принял изменения"
        );
        return toDto(entity);
    }

    private void applyFieldChange(Event entity, String field, Object newValue) throws EntityNotFoundException {
        switch (field) {
            case "eventName":
                entity.setEventName((String) newValue);
                break;
            case "eventDescription":
                entity.setEventDescription((String) newValue);
                break;
            case "eventDate":
                entity.setEventDate(LocalDateTime.parse((String) newValue));
                break;
            case "startTime":
                entity.setStartTime(LocalDateTime.parse((String) newValue));
                break;
            case "endTime":
                entity.setEndTime(LocalDateTime.parse((String) newValue));
                break;
            case "maxParticipants":
                entity.setMaxParticipants((Integer) newValue);
                break;
            case "imageUrl":
                entity.setImageUrl((String) newValue);
                break;
            case "price":
                entity.setPrice(new BigDecimal(newValue.toString()));
                break;
            case "eventFormat":
                entity.setEventFormat((EventFormat) newValue);
                break;
            case "eventCategoryId":
                entity.setEventCategory(findEventCategoryOrThrow(((Number) newValue).longValue()));
                break;
            case "placeId":
                Long placeId = newValue != null ? ((Number) newValue).longValue() : null;
                entity.setPlace(placeId != null ? findPlaceOrThrow(placeId) : null);
                break;
            default:
        }
    }

    @Transactional
    public void rejectChanges(Long eventId, String comment) throws EntityNotFoundException {
        Event entity = findOrThrow(eventId);
        Map<String, Object> changes = entity.getDraftChanges();
        List<String> rejectedFieldsRussian = new ArrayList<>();
        if (changes != null && !changes.isEmpty()) {
            for (String field : changes.keySet()) {
                rejectedFieldsRussian.add(mapFieldToRussian(field));
            }
        }
        entity.setDraftChanges(null);
        entity.setModerationStatus(ModerationStatus.PUBLISHED);
        eventRepository.save(entity);
        if (!rejectedFieldsRussian.isEmpty()) {
            emailService.sendEventChangesRejected(
                    entity.getOrganizer().getEmail(),
                    entity.getEventName(),
                    rejectedFieldsRussian,
                    comment != null ? comment : "Администратор отклонил изменения"
            );
        }
    }

    @Transactional
    public EventResponseDto verify(Long id, Boolean verified, String comment, Boolean sendEmail) throws EntityNotFoundException {
        Event entity = findOrThrow(id);
        entity.setVerified(verified);
        entity.setVerificationComment(comment);
        if (sendEmail != null && sendEmail) {
            if (verified) {
                emailService.sendEventApproved(entity.getOrganizer().getEmail(),
                        entity.getEventName(), comment);
            } else {
                emailService.sendEventRejected(entity.getOrganizer().getEmail(),
                        entity.getEventName(), comment);
            }
        }
        return toDto(eventRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) throws EntityNotFoundException {
        findOrThrow(id);
        eventRepository.deleteById(id);
    }

    @Transactional
    public void updateEventStatuses() throws EntityNotFoundException {
        LocalDateTime now = LocalDateTime.now();
        // Начинаем мероприятия, которые должны начаться (PLANNED -> ONGOING)
        List<Event> toStart = eventRepository.findEventsByStatusAndStartTimeBefore(EventStatus.PLANNED, now);
        for (Event event : toStart) {
            event.setEventStatus(EventStatus.ONGOING);
        }
        // Завершаем мероприятия, которые должны закончиться (ONGOING -> COMPLETED)
        List<Event> toEnd = eventRepository.findEventsByStatusAndEndTimeBefore(EventStatus.ONGOING, now);
        for (Event event : toEnd) {
            event.setEventStatus(EventStatus.COMPLETED);
        }
        eventRepository.saveAll(toStart);
        eventRepository.saveAll(toEnd);
    }

    private void validateDateTime(LocalDateTime startTime, LocalDateTime endTime) throws InvalidDateTimeException {
        if (startTime == null || endTime == null) {
            throw new InvalidDateTimeException(DateTimeErrorMessages.START_END_REQUIRED.getMessage());
        }
        if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
            throw new InvalidDateTimeException(DateTimeErrorMessages.START_MUST_BE_BEFORE_END.getMessage());
        }
    }

    private void updateEntityFromDto(Event entity, EventRequestDto dto) throws EntityNotFoundException {
        entity.setOrganizer(findUserOrThrow(dto.getIdOrganizer()));
        entity.setEventFormat(dto.getEventFormat());
        entity.setEventStatus(dto.getEventStatus());
        entity.setEventCategory(findEventCategoryOrThrow(dto.getIdEventCategory()));
        entity.setAdmin(dto.getIdAdmin() != null ? findUserOrThrow(dto.getIdAdmin()) : null);
        entity.setPlace(dto.getIdPlace() != null ? findPlaceOrThrow(dto.getIdPlace()) : null);
        entity.setEventName(dto.getEventName());
        entity.setEventDescription(dto.getEventDescription());
        entity.setEventDate(dto.getEventDate());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setMaxParticipants(dto.getMaxParticipants());
        entity.setImageUrl(dto.getImageUrl());
        entity.setPrice(dto.getPrice() != null ? dto.getPrice() : BigDecimal.ZERO);
        entity.setVerified(dto.getVerified() != null ? dto.getVerified() : false);
        entity.setVerificationComment(dto.getVerificationComment());
    }

    private Event findOrThrow(Long id) throws EntityNotFoundException {
        return eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(EntityType.EVENT.notFoundNeuter(id)));
    }

    private User findUserOrThrow(Long id) throws EntityNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(EntityType.USER.notFound(id)));
    }

    private EventCategory findEventCategoryOrThrow(Long id) throws EntityNotFoundException {
        return eventCategoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(EntityType.CATEGORY.notFoundFeminine(id)));
    }

    private Place findPlaceOrThrow(Long id) throws EntityNotFoundException {
        return placeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(EntityType.PLACE.notFoundNeuter(id)));
    }

    private EventResponseDto toDto(Event event) {
        return new EventResponseDto(
                event.getIdEvent(),
                event.getOrganizer().getIdUser(),
                event.getOrganizer().getFio(),
                event.getAdmin() != null ? event.getAdmin().getIdUser() : null,
                event.getAdmin() != null ? event.getAdmin().getFio() : null,
                event.getEventFormat(),
                event.getEventStatus(),
                event.getPlace() != null ? event.getPlace().getIdPlace() : null,
                event.getPlace() != null ? event.getPlace().getPlaceName() : null,
                event.getPlace() != null ? determinePlaceType(event.getPlace()) : null,
                event.getEventCategory().getIdEventCategory(),
                event.getEventCategory().getEventCategoryName(),
                event.getEventName(),
                event.getEventDescription(),
                event.getEventDate(),
                event.getStartTime(),
                event.getEndTime(),
                event.getMaxParticipants(),
                event.getImageUrl(),
                event.getPrice(),
                event.getVerified(),
                event.getVerificationComment(),
                event.getDraftChanges(),
                event.getModerationStatus().name()
        );
    }

    private EventShortDto toShortDto(Event event) {
        return new EventShortDto(
                event.getIdEvent(),
                event.getEventName(),
                event.getEventDate(),
                event.getStartTime(),
                event.getEndTime(),
                event.getEventFormat(),
                event.getEventStatus(),
                event.getEventCategory().getEventCategoryName(),
                event.getEventCategory().getIdEventCategory(),
                event.getPlace() != null ? event.getPlace().getPlaceName() : null,
                event.getPrice(),
                event.getImageUrl(),
                event.getVerified(),
                event.getModerationStatus() != null ? event.getModerationStatus().name() : null
        );
    }

    private String mapFieldToRussian(String field) {
        return switch (field) {
            case "eventName" -> "Название мероприятия";
            case "eventDescription" -> "Описание";
            case "eventDate" -> "Дата";
            case "startTime" -> "Время начала";
            case "endTime" -> "Время окончания";
            case "maxParticipants" -> "Максимальное количество участников";
            case "imageUrl" -> "Изображение";
            case "price" -> "Цена";
            case "eventFormat" -> "Формат мероприятия";
            case "eventCategoryId" -> "Категория";
            case "placeId" -> "Место проведения";
            default -> field;
        };
    }

    private String determinePlaceType(Place place) {
        if (place == null) return null;
        Class<?> clazz = Hibernate.getClass(place);
        if (clazz == PhysicalPlace.class) return "PHYSICAL";
        if (clazz == OnlinePlace.class) return "ONLINE";
        return "UNKNOWN";
    }
}
