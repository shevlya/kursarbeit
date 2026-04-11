package ru.ssau.srestapp.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.ssau.srestapp.entity.UserStatus;

@Data
public class StatusUpdateDto {
    @NotNull(message = "Статус пользователя обязателен")
    private UserStatus userStatus;
}
