package ru.ssau.srestapp.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserProfileUpdateDto {
    private Long idAvatar;

    @NotBlank(message = "ФИО обязательно")
    @Size(max = 255, message = "ФИО не должно превышать 255 символов")
    private String fio;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Неверный формат email")
    private String email;

    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate birthDate;

    private Boolean hasDisability = false;
}
