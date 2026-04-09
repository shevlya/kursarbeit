package ru.ssau.srestapp.dto.event;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import ru.ssau.srestapp.entity.EventFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EventSubmitChangesDto {

    private EventFormat eventFormat;
    private Long idEventCategory;
    private Long idPlace;

    private String eventName;
    private String eventDescription;

    @Future(message = "Дата мероприятия должна быть в будущем")
    private LocalDateTime eventDate;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Min(value = 1, message = "Количество участников должно быть не менее 1")
    private Integer maxParticipants;

    @Pattern(regexp = "^https?://.*$", message = "URL изображения должен начинаться с http:// или https://")
    private String imageUrl;

    @DecimalMin(value = "0.00", message = "Цена не может быть отрицательной")
    private BigDecimal price;
}
