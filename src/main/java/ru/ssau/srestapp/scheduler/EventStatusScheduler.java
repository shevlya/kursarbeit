package ru.ssau.srestapp.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.ssau.srestapp.exception.EntityNotFoundException;
import ru.ssau.srestapp.service.EventService;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventStatusScheduler {

    private final EventService eventService;

    private static final long UPDATE_INTERVAL = 5 * 60 * 1000;

    @Scheduled(fixedRate = UPDATE_INTERVAL)
    public void updateEventStatuses() {
        try {
            log.info("Запуск планового обновления статусов мероприятий");
            eventService.updateEventStatuses();
            log.info("Статусы мероприятий успешно обновлены");
        } catch (Exception e) {
            log.error("Критическая ошибка scheduler: {}", e.getMessage(), e);
        }
    }
}
