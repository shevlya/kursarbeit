package ru.ssau.srestapp.dto.userInterest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCategoryDto {
    private Long id;
    private String name;
    private String colorCode;
}
