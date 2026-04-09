package ru.ssau.srestapp.dto.organizerRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrganizerRequestRequestDto {
    @NotBlank(message = "Текст заявки обязателен")
    @Size(min = 20, message = "Описание должно содержать минимум 20 символов")
    private String requestText;
}
