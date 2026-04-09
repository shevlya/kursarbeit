package ru.ssau.srestapp.dto.place.onlinePlace;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class OnlinePlaceRequestDto {

    @NotBlank(message = "Название места обязательно")
    private String placeName;

    private String placeDescription;

    @NotBlank(message = "Ссылка на встречу обязательна")
    @Pattern(regexp = "^https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+$", message = "Неверный формат URL. Должен начинаться с http:// или https://")
    private String meetingUrl;

    private String specialNotes;
    private Boolean recording = false;
}
