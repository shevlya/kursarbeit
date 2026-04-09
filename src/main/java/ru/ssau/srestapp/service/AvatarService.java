package ru.ssau.srestapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.srestapp.dto.avatar.AvatarRequestDto;
import ru.ssau.srestapp.dto.avatar.AvatarResponseDto;
import ru.ssau.srestapp.entity.Avatar;
import ru.ssau.srestapp.exception.EntityNotFoundException;
import ru.ssau.srestapp.exception.EntityType;
import ru.ssau.srestapp.repository.AvatarRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvatarService {

    private final AvatarRepository avatarRepository;

    @Transactional(readOnly = true)
    public List<AvatarResponseDto> getAll() {
        return avatarRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AvatarResponseDto getById(Long id) throws EntityNotFoundException {
        return toDto(findOrThrow(id));
    }

    @Transactional
    public AvatarResponseDto create(AvatarRequestDto dto) {
        Avatar entity = new Avatar();
        entity.setAvatarUrl(dto.getAvatarUrl());
        return toDto(avatarRepository.save(entity));
    }

    @Transactional
    public AvatarResponseDto update(Long id, AvatarRequestDto dto) throws EntityNotFoundException {
        Avatar entity = findOrThrow(id);
        entity.setAvatarUrl(dto.getAvatarUrl());
        return toDto(avatarRepository.save(entity));
    }

    //на данный момент не используется на фронте, так как использую предустановленные
    @Transactional
    public void delete(Long id) throws EntityNotFoundException {
        findOrThrow(id);
        avatarRepository.deleteById(id);
    }

    private Avatar findOrThrow(Long id) throws EntityNotFoundException {
        return avatarRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(EntityType.AVATAR.notFound(id)));
    }

    private AvatarResponseDto toDto(Avatar e) {
        return new AvatarResponseDto(
                e.getIdAvatar(),
                e.getAvatarUrl()
        );
    }
}
