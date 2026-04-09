package ru.ssau.srestapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.srestapp.dto.place.onlinePlace.OnlinePlaceRequestDto;
import ru.ssau.srestapp.dto.place.onlinePlace.OnlinePlaceResponseDto;
import ru.ssau.srestapp.entity.OnlinePlace;
import ru.ssau.srestapp.exception.EntityNotFoundException;
import ru.ssau.srestapp.exception.EntityType;
import ru.ssau.srestapp.repository.OnlinePlaceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OnlinePlaceService {

    private final OnlinePlaceRepository onlinePlaceRepository;

    @Transactional(readOnly = true)
    public List<OnlinePlaceResponseDto> getAll() {
        return onlinePlaceRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public OnlinePlaceResponseDto getById(Long id) throws EntityNotFoundException {
        return toDto(findOrThrow(id));
    }

    @Transactional
    public OnlinePlaceResponseDto create(OnlinePlaceRequestDto dto) {
        OnlinePlace entity = new OnlinePlace();
        updateEntityFromDto(entity, dto);
        return toDto(onlinePlaceRepository.save(entity));
    }

    @Transactional
    public OnlinePlaceResponseDto update(Long id, OnlinePlaceRequestDto dto) throws EntityNotFoundException {
        OnlinePlace entity = findOrThrow(id);
        updateEntityFromDto(entity, dto);
        return toDto(onlinePlaceRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) throws EntityNotFoundException {
        findOrThrow(id);
        onlinePlaceRepository.deleteById(id);
    }

    private void updateEntityFromDto(OnlinePlace entity, OnlinePlaceRequestDto dto) {
        entity.setPlaceName(dto.getPlaceName());
        entity.setPlaceDescription(dto.getPlaceDescription());
        entity.setMeetingUrl(dto.getMeetingUrl());
        entity.setSpecialNotes(dto.getSpecialNotes());
        entity.setRecording(dto.getRecording());
    }

    private OnlinePlace findOrThrow(Long id) throws EntityNotFoundException {
        return onlinePlaceRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(EntityType.ONLINE_PLACE.notFoundNeuter(id)));
    }

    private OnlinePlaceResponseDto toDto(OnlinePlace e) {
        return new OnlinePlaceResponseDto(
                e.getIdPlace(),
                e.getPlaceName(),
                e.getPlaceDescription(),
                e.getMeetingUrl(),
                e.getSpecialNotes(),
                e.getRecording(),
                "ONLINE"
        );
    }
}
