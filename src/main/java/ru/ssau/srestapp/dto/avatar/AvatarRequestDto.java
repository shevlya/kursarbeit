package ru.ssau.srestapp.dto.avatar;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AvatarRequestDto {

    @NotBlank(message = "URL аватара обязателен")
    //@Pattern(regexp = "^(https?://)?([a-zA-Z0-9-]+\\.)*[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}(/.*)?$", message = "Неверный формат URL")
    private String avatarUrl;
}
