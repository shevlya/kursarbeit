package ru.ssau.srestapp.dto.eventParticipant;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.ssau.srestapp.entity.ParticipationStatus;

@Data
public class EventParticipantRequestDto {

    @NotNull(message = "ID пользователя обязателен")
    private Long userId;

    @NotNull(message = "ID мероприятия обязательно")
    private Long eventId;

    private ParticipationStatus participationStatus;

    private Boolean sendEmailNotification = false;
}
