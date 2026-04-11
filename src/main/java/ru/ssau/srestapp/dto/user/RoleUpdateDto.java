package ru.ssau.srestapp.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoleUpdateDto {
    @NotNull(message = "ID роли обязателен")
    private Long roleId;
}
