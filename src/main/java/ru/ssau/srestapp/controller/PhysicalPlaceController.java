package ru.ssau.srestapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.ssau.srestapp.dto.place.physicalPlace.PhysicalPlaceRequestDto;
import ru.ssau.srestapp.dto.place.physicalPlace.PhysicalPlaceResponseDto;
import ru.ssau.srestapp.exception.EntityNotFoundException;
import ru.ssau.srestapp.service.PhysicalPlaceService;

import java.util.List;

@RestController
@RequestMapping("/api/physical-places")
@RequiredArgsConstructor
public class PhysicalPlaceController {

    private final PhysicalPlaceService physicalPlaceService;

    @GetMapping
    public List<PhysicalPlaceResponseDto> getAll() {
        return physicalPlaceService.getAll();
    }

    @GetMapping("/{id}")
    public PhysicalPlaceResponseDto getById(@PathVariable Long id) throws EntityNotFoundException {
        return physicalPlaceService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PhysicalPlaceResponseDto create(@Valid @RequestBody PhysicalPlaceRequestDto dto) {
        return physicalPlaceService.create(dto);
    }

    @PutMapping("/{id}")
    public PhysicalPlaceResponseDto update(
            @PathVariable Long id,
            @Valid @RequestBody PhysicalPlaceRequestDto dto) throws EntityNotFoundException {
        return physicalPlaceService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException {
        physicalPlaceService.delete(id);
    }
}
