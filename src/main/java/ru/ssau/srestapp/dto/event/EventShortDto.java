package ru.ssau.srestapp.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.ssau.srestapp.entity.EventFormat;
import ru.ssau.srestapp.entity.EventStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EventShortDto {
    private Long idEvent;
    private String eventName;
    private LocalDateTime eventDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private EventFormat eventFormat;
    private EventStatus eventStatus;
    private String eventCategoryName;
    private Long idEventCategory;
    private String placeName;
    private BigDecimal price;
    private String imageUrl;
    private Boolean verified;
    private String moderationStatus;
}
