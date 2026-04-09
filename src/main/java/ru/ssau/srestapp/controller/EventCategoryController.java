package ru.ssau.srestapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.ssau.srestapp.dto.eventCategory.EventCategoryRequestDto;
import ru.ssau.srestapp.dto.eventCategory.EventCategoryResponseDto;
import ru.ssau.srestapp.exception.DuplicateEntityException;
import ru.ssau.srestapp.exception.EntityNotFoundException;
import ru.ssau.srestapp.service.EventCategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class EventCategoryController {

    private final EventCategoryService eventCategoryService;

    @GetMapping
    public List<EventCategoryResponseDto> getAll() {
        return eventCategoryService.getAll();
    }

    @GetMapping("/{id}")
    public EventCategoryResponseDto getById(@PathVariable Long id) throws EntityNotFoundException {
        return eventCategoryService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventCategoryResponseDto create(@Valid @RequestBody EventCategoryRequestDto dto) throws DuplicateEntityException {
        return eventCategoryService.create(dto);
    }

    @PutMapping("/{id}")
    public EventCategoryResponseDto update(@PathVariable Long id, @Valid @RequestBody EventCategoryRequestDto dto) throws DuplicateEntityException, EntityNotFoundException {
        return eventCategoryService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException {
        eventCategoryService.delete(id);
    }
}
