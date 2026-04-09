package ru.ssau.srestapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.ssau.srestapp.dto.place.onlinePlace.OnlinePlaceRequestDto;
import ru.ssau.srestapp.dto.place.onlinePlace.OnlinePlaceResponseDto;
import ru.ssau.srestapp.exception.EntityNotFoundException;
import ru.ssau.srestapp.service.OnlinePlaceService;

import java.util.List;

@RestController
@RequestMapping("/api/online-places")
@RequiredArgsConstructor
public class OnlinePlaceController {

    private final OnlinePlaceService onlinePlaceService;

    @GetMapping
    public List<OnlinePlaceResponseDto> getAll() {
        return onlinePlaceService.getAll();
    }

    @GetMapping("/{id}")
    public OnlinePlaceResponseDto getById(@PathVariable Long id) throws EntityNotFoundException {
        return onlinePlaceService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OnlinePlaceResponseDto create(@Valid @RequestBody OnlinePlaceRequestDto dto) {
        return onlinePlaceService.create(dto);
    }

    @PutMapping("/{id}")
    public OnlinePlaceResponseDto update(
            @PathVariable Long id,
            @Valid @RequestBody OnlinePlaceRequestDto dto) throws EntityNotFoundException {
        return onlinePlaceService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException {
        onlinePlaceService.delete(id);
    }
}
