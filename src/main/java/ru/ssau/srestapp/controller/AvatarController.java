package ru.ssau.srestapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.ssau.srestapp.dto.avatar.AvatarRequestDto;
import ru.ssau.srestapp.dto.avatar.AvatarResponseDto;
import ru.ssau.srestapp.exception.EntityNotFoundException;
import ru.ssau.srestapp.service.AvatarService;

import java.util.List;

@RestController
@RequestMapping("/api/avatars")
@RequiredArgsConstructor
public class AvatarController {

    private final AvatarService avatarService;

    @GetMapping
    public List<AvatarResponseDto> getAll() {
        return avatarService.getAll();
    }

    @GetMapping("/{id}")
    public AvatarResponseDto getById(@PathVariable Long id) throws EntityNotFoundException {
        return avatarService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AvatarResponseDto create(@Valid @RequestBody AvatarRequestDto dto) {
        return avatarService.create(dto);
    }

    @PutMapping("/{id}")
    public AvatarResponseDto update(@PathVariable Long id, @Valid @RequestBody AvatarRequestDto dto) throws EntityNotFoundException {
        return avatarService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException {
        avatarService.delete(id);
    }
}
