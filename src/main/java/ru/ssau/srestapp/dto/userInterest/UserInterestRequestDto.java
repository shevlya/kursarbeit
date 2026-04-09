package ru.ssau.srestapp.dto.userInterest;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserInterestRequestDto {
    @NotNull(message = "ID категории обязателен")
    private Long eventCategoryId;
}
