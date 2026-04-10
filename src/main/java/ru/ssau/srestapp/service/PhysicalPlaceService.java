package ru.ssau.srestapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.srestapp.dto.place.physicalPlace.PhysicalPlaceRequestDto;
import ru.ssau.srestapp.dto.place.physicalPlace.PhysicalPlaceResponseDto;
import ru.ssau.srestapp.entity.PhysicalPlace;
import ru.ssau.srestapp.exception.EntityNotFoundException;
import ru.ssau.srestapp.exception.EntityType;
import ru.ssau.srestapp.repository.PhysicalPlaceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PhysicalPlaceService {

    private static final String PLACE_TYPE_PHYSICAL = "PHYSICAL";

    private final PhysicalPlaceRepository physicalPlaceRepository;

    @Transactional(readOnly = true)
    public List<PhysicalPlaceResponseDto> getAll() {
        return physicalPlaceRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public PhysicalPlaceResponseDto getById(Long id) throws EntityNotFoundException {
        return toDto(findOrThrow(id));
    }

    @Transactional
    public PhysicalPlaceResponseDto create(PhysicalPlaceRequestDto dto) {
        PhysicalPlace entity = new PhysicalPlace();
        updateEntityFromDto(entity, dto);
        return toDto(physicalPlaceRepository.save(entity));
    }

    @Transactional
    public PhysicalPlaceResponseDto update(Long id, PhysicalPlaceRequestDto dto) throws EntityNotFoundException {
        PhysicalPlace entity = findOrThrow(id);
        updateEntityFromDto(entity, dto);
        return toDto(physicalPlaceRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) throws EntityNotFoundException {
        findOrThrow(id);
        physicalPlaceRepository.deleteById(id);
    }

    private void updateEntityFromDto(PhysicalPlace entity, PhysicalPlaceRequestDto dto) {
        entity.setPlaceName(dto.getPlaceName());
        entity.setPlaceDescription(dto.getPlaceDescription());
        entity.setAddress(dto.getAddress());
        entity.setDisabilityAccessible(dto.getDisabilityAccessible());
    }

    private PhysicalPlace findOrThrow(Long id) throws EntityNotFoundException {
        return physicalPlaceRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(EntityType.PHYSICAL_PLACE.notFoundNeuter(id)));
    }

    private PhysicalPlaceResponseDto toDto(PhysicalPlace e) {
        return new PhysicalPlaceResponseDto(
                e.getIdPlace(),
                e.getPlaceName(),
                e.getPlaceDescription(),
                e.getAddress(),
                e.getDisabilityAccessible(),
                PLACE_TYPE_PHYSICAL
        );
    }
}
