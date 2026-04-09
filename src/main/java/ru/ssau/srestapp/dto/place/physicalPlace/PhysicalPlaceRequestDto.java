package ru.ssau.srestapp.dto.place.physicalPlace;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PhysicalPlaceRequestDto {

    @NotBlank(message = "Название места обязательно")
    private String placeName;

    private String placeDescription;

    @NotBlank(message = "Адрес обязателен")
    private String address;

    private Boolean disabilityAccessible = false;
}
