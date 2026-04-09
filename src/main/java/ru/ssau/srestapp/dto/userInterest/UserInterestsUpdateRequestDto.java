package ru.ssau.srestapp.dto.userInterest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UserInterestsUpdateRequestDto {
    @NotNull(message = "Список категорий обязателен")
    @Size(max = 5, message = "Нельзя выбрать более 5 категорий")
    private List<Long> categoryIds;
}
