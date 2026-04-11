package ru.ssau.srestapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.srestapp.dto.event.EventShortDto;
import ru.ssau.srestapp.entity.Event;
import ru.ssau.srestapp.entity.UserInterest;
import ru.ssau.srestapp.repository.EventRepository;
import ru.ssau.srestapp.repository.UserInterestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final EventRepository eventRepository;
    private final UserInterestRepository userInterestRepository;
    private final EventService eventService;

    @Transactional(readOnly = true)
    public List<EventShortDto> getRecommendedEvents(Long userId, int limit) {
        LocalDateTime now = LocalDateTime.now().toLocalDate().atStartOfDay();
        List<Event> activeEvents = eventRepository.findActiveAndVerified(now);
        if (userId != null) {
            List<UserInterest> interests = userInterestRepository.findByUserId(userId);
            if (!interests.isEmpty()) {
                Set<Long> categoryIds = interests.stream()
                        .map(ui -> ui.getIdEventCategory().getIdEventCategory())
                        .collect(Collectors.toSet());
                List<Event> matched = activeEvents.stream()
                        .filter(e -> categoryIds.contains(e.getEventCategory().getIdEventCategory()))
                        .limit(limit)
                        .toList();
                if (!matched.isEmpty()) {
                    return matched.stream()
                            .map(this::toShortDto)
                            .toList();
                }
            }
        }
        return activeEvents.stream()
                .limit(limit)
                .map(this::toShortDto)
                .toList();
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
}
