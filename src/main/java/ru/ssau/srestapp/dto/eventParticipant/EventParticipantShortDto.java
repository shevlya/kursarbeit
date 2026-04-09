package ru.ssau.srestapp.dto.eventParticipant;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.ssau.srestapp.entity.ParticipationStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EventParticipantShortDto {
    private Long userId;
    private String userFio;
    private Long eventId;
    private String eventName;
    private ParticipationStatus participationStatus;
    private LocalDateTime registrationDate;
}
