package ru.ssau.srestapp.dto.organizerRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.ssau.srestapp.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrganizerRequestShortDto {
    private Long idOrganizerRequest;
    private Long userId;
    private String userFio;
    private String userEmail;
    private RequestStatus requestStatus;
    private String requestText;
    private LocalDateTime submittedAt;
}
