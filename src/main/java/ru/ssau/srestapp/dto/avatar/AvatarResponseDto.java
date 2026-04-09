package ru.ssau.srestapp.dto.avatar;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AvatarResponseDto {
    private Long idAvatar;
    private String avatarUrl;
}
