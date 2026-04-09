package ru.ssau.srestapp.dto.role;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoleResponseDto {
    private Long idRole;
    private String roleName;
    private String roleDescription;
}
