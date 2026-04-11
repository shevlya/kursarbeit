package ru.ssau.srestapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.srestapp.dto.user.*;
import ru.ssau.srestapp.entity.*;
import ru.ssau.srestapp.exception.*;
import ru.ssau.srestapp.repository.AvatarRepository;
import ru.ssau.srestapp.repository.RoleRepository;
import ru.ssau.srestapp.repository.UserRepository;
import ru.ssau.srestapp.util.SecurityUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AvatarRepository avatarRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAll() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public UserResponseDto getById(Long id) throws EntityNotFoundException {
        return toDto(findOrThrow(id));
    }

    @Transactional
    public UserResponseDto create(UserRequestDto dto) throws EmailAlreadyExistsException, EntityNotFoundException {
        checkUniqueEmail(dto.getEmail());
        User entity = new User();
        entity.setUserStatus(dto.getUserStatus());
        entity.setRole(findRoleOrThrow(dto.getIdRole()));
        entity.setAvatar(getAvatarOrNull(dto.getIdAvatar()));
        entity.setFio(dto.getFio());
        entity.setEmail(dto.getEmail());
        entity.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        entity.setBirthDate(dto.getBirthDate());
        entity.setHasDisability(dto.getHasDisability());
        emailService.sendWelcome(entity.getEmail(), entity.getFio());
        return toDto(userRepository.save(entity));
    }

    @Transactional
    public UserResponseDto updateByAdmin(Long id, UserRequestDto dto) throws EmailAlreadyExistsException, EntityNotFoundException {
        User entity = findOrThrow(id);
        updateEmailIfChanged(entity, dto.getEmail());
        updateUserFromRequest(entity, dto);
        entity.setUserStatus(dto.getUserStatus());
        entity.setRole(findRoleOrThrow(dto.getIdRole()));
        return toDto(userRepository.save(entity));
    }

    @Transactional
    public UserResponseDto update(Long id, UserRequestDto dto) throws EmailAlreadyExistsException, EntityNotFoundException, AccessDeniedException {
        ensureCurrentUserHasAccess(id);
        User entity = findOrThrow(id);
        updateEmailIfChanged(entity, dto.getEmail());
        updateUserFromRequest(entity, dto);
        return toDto(userRepository.save(entity));
    }

    @Transactional
    public UserResponseDto updateProfile(Long userId, UserProfileUpdateDto dto) throws EmailAlreadyExistsException, EntityNotFoundException, AccessDeniedException {
        ensureCurrentUserHasAccess(userId);
        User entity = findOrThrow(userId);
        updateEmailIfChanged(entity, dto.getEmail());
        updateUserFromProfileDto(entity, dto);
        return toDto(userRepository.save(entity));
    }

    @Transactional
    public UserResponseDto updateDisability(Long userId, Boolean hasDisability) throws EntityNotFoundException, AccessDeniedException {
        ensureCurrentUserHasAccess(userId);
        User entity = findOrThrow(userId);
        entity.setHasDisability(hasDisability);
        return toDto(userRepository.save(entity));
    }

    @Transactional
    public void changePasswordForCurrentUser(PasswordChangeRequestDto dto) throws EntityNotFoundException, InvalidPasswordException, PasswordSameAsOldException {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User user = findOrThrow(currentUserId);
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPasswordHash())) {
            throw new InvalidPasswordException();
        }
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPasswordHash())) {
            throw new PasswordSameAsOldException();
        }
        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) throws EntityNotFoundException {
        findOrThrow(id);
        userRepository.deleteById(id);
    }

    @Transactional
    public UserResponseDto updateUserStatus(Long userId, UserStatus newStatus) throws EntityNotFoundException, AccessDeniedException {
        ensureNotSelfModification(userId);
        User user = findOrThrow(userId);
        user.setUserStatus(newStatus);
        return toDto(userRepository.save(user));
    }

    @Transactional
    public UserResponseDto updateUserRole(Long userId, Long roleId) throws EntityNotFoundException, AccessDeniedException {
        ensureNotSelfModification(userId);
        User user = findOrThrow(userId);
        Role newRole = findRoleOrThrow(roleId);
        user.setRole(newRole);
        return toDto(userRepository.save(user));
    }

    private void ensureNotSelfModification(Long targetUserId) throws AccessDeniedException {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId.equals(targetUserId)) {
            throw new AccessDeniedException("Вы не можете изменять свой собственный статус или роль");
        }
    }

    private void ensureCurrentUserHasAccess(Long targetUserId) throws AccessDeniedException {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!currentUserId.equals(targetUserId)) {
            throw new AccessDeniedException("Доступ запрещён: вы можете редактировать только свой профиль");
        }
    }

    private void updateEmailIfChanged(User user, String newEmail) throws EmailAlreadyExistsException {
        if (!user.getEmail().equalsIgnoreCase(newEmail)) {
            checkUniqueEmail(newEmail);
            user.setEmail(newEmail);
        }
    }

    private void updateUserFromRequest(User user, UserRequestDto dto) throws EntityNotFoundException {
        user.setAvatar(getAvatarOrNull(dto.getIdAvatar()));
        user.setFio(dto.getFio());
        user.setBirthDate(dto.getBirthDate());
        user.setHasDisability(dto.getHasDisability());
    }

    private void updateUserFromProfileDto(User user, UserProfileUpdateDto dto) throws EntityNotFoundException {
        user.setAvatar(getAvatarOrNull(dto.getIdAvatar()));
        user.setFio(dto.getFio());
        user.setBirthDate(dto.getBirthDate());
        user.setHasDisability(dto.getHasDisability());
    }

    private Avatar getAvatarOrNull(Long avatarId) throws EntityNotFoundException {
        return avatarId != null ? findAvatarOrThrow(avatarId) : null;
    }

    private User findOrThrow(Long id) throws EntityNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(EntityType.USER.notFound(id)));
    }

    private Role findRoleOrThrow(Long id) throws EntityNotFoundException {
        return roleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(EntityType.ROLE.notFoundFeminine(id)));
    }

    private Avatar findAvatarOrThrow(Long id) throws EntityNotFoundException {
        return avatarRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(EntityType.AVATAR.notFound(id)));
    }

    private void checkUniqueEmail(String email) throws EmailAlreadyExistsException {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(EntityType.USER.duplicateEmail(email));
        }
    }

    private UserResponseDto toDto(User user) {
        Avatar avatar = user.getAvatar();
        Long avatarId = avatar != null ? avatar.getIdAvatar() : null;
        String avatarUrl = avatar != null ? avatar.getAvatarUrl() : null;
        return new UserResponseDto(
                user.getIdUser(),
                user.getUserStatus(),
                avatarId,
                avatarUrl,
                user.getRole().getIdRole(),
                user.getRole().getRoleName(),
                user.getFio(),
                user.getEmail(),
                user.getBirthDate(),
                user.getHasDisability()
        );
    }
}
