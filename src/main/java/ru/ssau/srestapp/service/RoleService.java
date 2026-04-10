package ru.ssau.srestapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.srestapp.dto.role.RoleRequestDto;
import ru.ssau.srestapp.dto.role.RoleResponseDto;
import ru.ssau.srestapp.entity.Role;
import ru.ssau.srestapp.exception.DuplicateEntityException;
import ru.ssau.srestapp.exception.EntityNotFoundException;
import ru.ssau.srestapp.exception.EntityType;
import ru.ssau.srestapp.repository.RoleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public List<RoleResponseDto> getAll() {
        return roleRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoleResponseDto getById(Long id) throws EntityNotFoundException {
        return toDto(findOrThrow(id));
    }

    @Transactional
    public RoleResponseDto create(RoleRequestDto dto) throws DuplicateEntityException {
        checkUniqueName(dto.getRoleName());
        Role entity = new Role();
        updateEntityFromDto(entity, dto);
        log.info("Создана роль: {}", dto.getRoleName());
        return toDto(roleRepository.save(entity));
    }

    @Transactional
    public RoleResponseDto update(Long id, RoleRequestDto dto) throws DuplicateEntityException, EntityNotFoundException {
        Role entity = findOrThrow(id);
        boolean nameChanged = !entity.getRoleName().equalsIgnoreCase(dto.getRoleName());
        if (nameChanged) {
            checkUniqueName(dto.getRoleName());
        }
        updateEntityFromDto(entity, dto);
        log.info("Обновлена роль: {}", dto.getRoleName());
        return toDto(roleRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) throws EntityNotFoundException {
        findOrThrow(id);
        roleRepository.deleteById(id);
        log.info("Удалена роль с id={}", id);
    }

    private void checkUniqueName(String name) throws DuplicateEntityException {
        if (roleRepository.existsByRoleName(name)) {
            throw new DuplicateEntityException(EntityType.ROLE.duplicate(name));
        }
    }

    private void updateEntityFromDto(Role entity, RoleRequestDto dto) {
        entity.setRoleName(dto.getRoleName());
        entity.setRoleDescription(dto.getRoleDescription());
    }

    private Role findOrThrow(Long id) throws EntityNotFoundException {
        return roleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(EntityType.ROLE.notFoundFeminine(id)));
    }

    private RoleResponseDto toDto(Role e) {
        return new RoleResponseDto(
                e.getIdRole(),
                e.getRoleName(),
                e.getRoleDescription()
        );
    }
}
