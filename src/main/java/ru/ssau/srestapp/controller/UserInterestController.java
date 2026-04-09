package ru.ssau.srestapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.ssau.srestapp.dto.userInterest.*;
import ru.ssau.srestapp.exception.DuplicateEntityException;
import ru.ssau.srestapp.exception.EntityNotFoundException;
import ru.ssau.srestapp.service.UserInterestService;

import java.util.List;

@RestController
@RequestMapping("/api/users/me/interests")
@RequiredArgsConstructor
public class UserInterestController {

    private final UserInterestService userInterestService;

    @GetMapping
    public List<UserCategoryDto> getMyInterests() {
        return userInterestService.getCategoriesForCurrentUser();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserInterestResponseDto addInterest(@Valid @RequestBody UserInterestRequestDto dto) throws EntityNotFoundException, DuplicateEntityException {
        return userInterestService.addInterestForCurrentUser(dto);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeInterest(@PathVariable Long categoryId) throws EntityNotFoundException {
        userInterestService.removeInterestForCurrentUser(categoryId);
    }

    @PutMapping
    public List<UserCategoryDto> updateAllInterests(@Valid @RequestBody UserInterestsUpdateRequestDto dto) throws EntityNotFoundException {
        return userInterestService.updateInterestsForCurrentUser(dto);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearAllInterests() {
        userInterestService.deleteAllInterestsForCurrentUser();
    }

    @GetMapping("/user/{userId}")
    public List<UserCategoryDto> getUserInterests(@PathVariable Long userId) throws EntityNotFoundException {
        return userInterestService.getCategoriesByUserId(userId);
    }
}
