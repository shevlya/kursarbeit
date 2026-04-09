package ru.ssau.srestapp.dto.eventCategory;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventCategoryResponseDto {
    private Long idEventCategory;
    private String eventCategoryName;
    private String eventCategoryDescription;
    private String colorCode;
}
