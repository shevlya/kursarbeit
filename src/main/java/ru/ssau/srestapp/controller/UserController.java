package ru.ssau.srestapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.ssau.srestapp.dto.user.*;
import ru.ssau.srestapp.exception.*;
import ru.ssau.srestapp.service.UserService;
import ru.ssau.srestapp.util.SecurityUtils;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponseDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserResponseDto getById(@PathVariable Long id) throws EntityNotFoundException {
        return userService.getById(id);
    }

    @PutMapping("/{id}")
    public UserResponseDto updateByAdmin(@PathVariable Long id, @Valid @RequestBody UserRequestDto userRequestDto) throws DuplicateEntityException, EntityNotFoundException {
        return userService.updateByAdmin(id, userRequestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByAdmin(@PathVariable Long id) throws EntityNotFoundException {
        userService.delete(id);
    }

    @GetMapping("/me")
    public UserResponseDto getCurrentUser() throws EntityNotFoundException {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return userService.getById(currentUserId);
    }

    @PutMapping("/me")
    public UserResponseDto updateCurrentUser(@Valid @RequestBody UserProfileUpdateDto userProfileUpdateDto) throws DuplicateEntityException, EntityNotFoundException, AccessDeniedException {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return userService.updateProfile(currentUserId, userProfileUpdateDto);
    }

    @PatchMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@Valid @RequestBody PasswordChangeRequestDto passwordChangeRequestDto) throws EntityNotFoundException, PasswordSameAsOldException, InvalidPasswordException {
        userService.changePasswordForCurrentUser(passwordChangeRequestDto);
    }

    @PatchMapping("/me/disability")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto updateDisability(@RequestBody DisabilityUpdateDto disabilityUpdateDto) throws EntityNotFoundException, AccessDeniedException {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return userService.updateDisability(currentUserId, disabilityUpdateDto.getHasDisability());
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto updateUserStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateDto dto) throws EntityNotFoundException, AccessDeniedException {
        return userService.updateUserStatus(id, dto.getUserStatus());
    }

    @PatchMapping("/{id}/role")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto updateUserRole(@PathVariable Long id, @Valid @RequestBody RoleUpdateDto dto) throws EntityNotFoundException, AccessDeniedException {
        return userService.updateUserRole(id, dto.getRoleId());
    }
}
