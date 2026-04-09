package ru.ssau.srestapp.dto.event;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.ssau.srestapp.entity.EventFormat;
import ru.ssau.srestapp.entity.EventStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EventRequestDto {

    @NotNull(message = "ID организатора обязателен")
    private Long idOrganizer;

    private Long idAdmin;

    @NotNull(message = "Формат мероприятия обязателен")
    private EventFormat eventFormat;

    @NotNull(message = "Статус мероприятия обязателен")
    private EventStatus eventStatus;

    private Long idPlace;

    @NotNull(message = "ID категории мероприятия обязателен")
    private Long idEventCategory;

    @NotBlank(message = "Название мероприятия обязательно")
    private String eventName;

    private String eventDescription;

    @NotNull(message = "Дата мероприятия обязательна")
    @Future(message = "Дата мероприятия должна быть в будущем")
    private LocalDateTime eventDate;

    @NotNull(message = "Время начала обязательно")
    private LocalDateTime startTime;

    @NotNull(message = "Время окончания обязательно")
    private LocalDateTime endTime;

    @Min(value = 1, message = "Количество участников должно быть не менее 1")
    private Integer maxParticipants;

    @Pattern(regexp = "^https?://.*$", message = "URL изображения должен начинаться с http:// или https://")
    private String imageUrl;

    @DecimalMin(value = "0.00", message = "Цена не может быть отрицательной")
    private BigDecimal price = BigDecimal.ZERO;

    private Boolean verified = false;

    private String verificationComment;
}
