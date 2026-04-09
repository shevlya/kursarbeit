package ru.ssau.srestapp.dto.organizerRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.ssau.srestapp.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrganizerRequestResponseDto {
    private Long idOrganizerRequest;
    private Long userId;
    private String userFio;
    private RequestStatus requestStatus;
    private String requestText;
    private String reviewComment;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
}
