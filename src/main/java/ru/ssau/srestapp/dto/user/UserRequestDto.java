package ru.ssau.srestapp.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.ssau.srestapp.entity.UserStatus;

import java.time.LocalDate;

@Data
public class UserRequestDto {

    @NotNull(message = "Статус пользователя обязателен")
    private UserStatus userStatus;

    private Long idAvatar;

    @NotNull(message = "ID роли обязателен")
    private Long idRole;

    @NotBlank(message = "ФИО обязательно")
    @Size(max = 255, message = "ФИО не должно превышать 255 символов")
    private String fio;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Неверный формат email")
    private String email;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, message = "Пароль должен содержать от 6 символов")
    private String password;

    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate birthDate;

    private Boolean hasDisability = false;
}
