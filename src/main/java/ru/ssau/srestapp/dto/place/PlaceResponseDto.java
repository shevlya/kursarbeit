package ru.ssau.srestapp.dto.place;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaceResponseDto {
    private Long idPlace;
    private String placeName;
    private String placeDescription;
    private String type; // "PHYSICAL" или "ONLINE"
}
