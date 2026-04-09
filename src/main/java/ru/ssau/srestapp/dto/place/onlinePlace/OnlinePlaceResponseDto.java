package ru.ssau.srestapp.dto.place.onlinePlace;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OnlinePlaceResponseDto {
    private Long idPlace;
    private String placeName;
    private String placeDescription;
    private String meetingUrl;
    private String specialNotes;
    private Boolean recording;
    private String type = "ONLINE";
}
