package ru.ssau.srestapp.dto.place.physicalPlace;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PhysicalPlaceResponseDto {
    private Long idPlace;
    private String placeName;
    private String placeDescription;
    private String address;
    private Boolean disabilityAccessible;
    private String type = "PHYSICAL";
}
