package ru.ssau.srestapp.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.ssau.srestapp.entity.EventFormat;
import ru.ssau.srestapp.entity.EventStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
public class EventResponseDto {
    private Long idEvent;
    private Long idOrganizer;
    private String organizerFio;
    private Long idAdmin;
    private String adminFio;
    private EventFormat eventFormat;
    private EventStatus eventStatus;
    private Long idPlace;
    private String placeName;
    private String placeType;
    private Long idEventCategory;
    private String eventCategoryName;
    private String eventName;
    private String eventDescription;
    private LocalDateTime eventDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxParticipants;
    private String imageUrl;
    private BigDecimal price;
    private Boolean verified;
    private String verificationComment;
    private Map<String, Object> draftChanges;
    private String moderationStatus;
}
