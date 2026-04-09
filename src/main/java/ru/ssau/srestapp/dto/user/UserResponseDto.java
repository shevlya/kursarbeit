package ru.ssau.srestapp.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.ssau.srestapp.entity.UserStatus;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UserResponseDto {
    private Long idUser;
    private UserStatus userStatus;
    private Long idAvatar;
    private String avatarUrl;
    private Long idRole;
    private String roleName;
    private String fio;
    private String email;
    private LocalDate birthDate;
    private Boolean hasDisability;
}
