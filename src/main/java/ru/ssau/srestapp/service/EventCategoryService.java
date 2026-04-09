package ru.ssau.srestapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.srestapp.dto.eventCategory.EventCategoryRequestDto;
import ru.ssau.srestapp.dto.eventCategory.EventCategoryResponseDto;
import ru.ssau.srestapp.entity.EventCategory;
import ru.ssau.srestapp.exception.DuplicateEntityException;
import ru.ssau.srestapp.exception.EntityNotFoundException;
import ru.ssau.srestapp.exception.EntityType;
import ru.ssau.srestapp.repository.EventCategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventCategoryService {

    private final EventCategoryRepository eventCategoryRepository;

    @Transactional(readOnly = true)
    public List<EventCategoryResponseDto> getAll() {
        return eventCategoryRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public EventCategoryResponseDto getById(Long id) throws EntityNotFoundException {
        return toDto(findOrThrow(id));
    }

    @Transactional
    public EventCategoryResponseDto create(EventCategoryRequestDto dto) throws DuplicateEntityException {
        checkUniqueName(dto.getEventCategoryName());
        EventCategory entity = new EventCategory();
        updateEntityFromDto(entity, dto);
        return toDto(eventCategoryRepository.save(entity));
    }

    @Transactional
    public EventCategoryResponseDto update(Long id, EventCategoryRequestDto dto) throws DuplicateEntityException, EntityNotFoundException {
        EventCategory entity = findOrThrow(id);
        boolean nameChanged = !entity.getEventCategoryName().equalsIgnoreCase(dto.getEventCategoryName());
        if (nameChanged) {
            checkUniqueName(dto.getEventCategoryName());
        }
        updateEntityFromDto(entity, dto);
        return toDto(eventCategoryRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) throws EntityNotFoundException {
        findOrThrow(id);
        eventCategoryRepository.deleteById(id);
    }

    private void updateEntityFromDto(EventCategory entity, EventCategoryRequestDto dto) {
        entity.setEventCategoryName(dto.getEventCategoryName());
        entity.setEventCategoryDescription(dto.getEventCategoryDescription());
        entity.setColorCode(dto.getColorCode());
    }

    private EventCategory findOrThrow(Long id) throws EntityNotFoundException {
        return eventCategoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(EntityType.CATEGORY.notFoundFeminine(id)));
    }

    private void checkUniqueName(String name) throws DuplicateEntityException {
        if (eventCategoryRepository.existsByEventCategoryName(name)) {
            throw new DuplicateEntityException(EntityType.CATEGORY.duplicate(name));
        }
    }

    private EventCategoryResponseDto toDto(EventCategory e) {
        return new EventCategoryResponseDto(
                e.getIdEventCategory(),
                e.getEventCategoryName(),
                e.getEventCategoryDescription(),
                e.getColorCode()
        );
    }
}
