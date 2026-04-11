package ru.ssau.srestapp.dto.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContactMessageRequestDto {

    @NotBlank(message = "Имя обязательно")
    @Size(max = 100, message = "Имя не должно превышать 100 символов")
    private String name;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Неверный формат email")
    private String email;

    @NotBlank(message = "Тема обязательна")
    @Size(max = 200, message = "Тема не должна превышать 200 символов")
    private String subject;

    @NotBlank(message = "Сообщение обязательно")
    @Size(min = 10, max = 2000, message = "Сообщение должно содержать от 10 до 2000 символов")
    private String message;
}
