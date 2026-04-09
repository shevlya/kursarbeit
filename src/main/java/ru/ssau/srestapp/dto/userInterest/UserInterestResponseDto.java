package ru.ssau.srestapp.dto.userInterest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInterestResponseDto {
    private Long userId;
    private String userFio;
    private Long categoryId;
    private String categoryName;
    private String colorCode;
}
