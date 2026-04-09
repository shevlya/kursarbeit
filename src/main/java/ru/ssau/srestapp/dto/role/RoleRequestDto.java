package ru.ssau.srestapp.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RoleRequestDto {

    @NotBlank(message = "Название роли обязательно")
    @Size(max = 50, message = "Название не должно превышать 50 символов")
    private String roleName;

    private String roleDescription;
}
