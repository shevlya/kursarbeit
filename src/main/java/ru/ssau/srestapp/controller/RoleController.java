package ru.ssau.srestapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.ssau.srestapp.dto.role.RoleRequestDto;
import ru.ssau.srestapp.dto.role.RoleResponseDto;
import ru.ssau.srestapp.exception.DuplicateEntityException;
import ru.ssau.srestapp.exception.EntityNotFoundException;
import ru.ssau.srestapp.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public List<RoleResponseDto> getAll() {
        return roleService.getAll();
    }

    @GetMapping("/{id}")
    public RoleResponseDto getById(@PathVariable Long id) throws EntityNotFoundException {
        return roleService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoleResponseDto create(@Valid @RequestBody RoleRequestDto dto) throws DuplicateEntityException {
        return roleService.create(dto);
    }

    @PutMapping("/{id}")
    public RoleResponseDto update(@PathVariable Long id, @Valid @RequestBody RoleRequestDto dto) throws DuplicateEntityException, EntityNotFoundException {
        return roleService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException {
        roleService.delete(id);
    }
}
