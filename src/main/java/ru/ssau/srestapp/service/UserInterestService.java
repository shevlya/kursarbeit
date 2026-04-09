package ru.ssau.srestapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.srestapp.dto.userInterest.UserCategoryDto;
import ru.ssau.srestapp.dto.userInterest.UserInterestRequestDto;
import ru.ssau.srestapp.dto.userInterest.UserInterestResponseDto;
import ru.ssau.srestapp.dto.userInterest.UserInterestsUpdateRequestDto;
import ru.ssau.srestapp.entity.EventCategory;
import ru.ssau.srestapp.entity.User;
import ru.ssau.srestapp.entity.UserInterest;
import ru.ssau.srestapp.exception.DuplicateEntityException;
import ru.ssau.srestapp.exception.EntityNotFoundException;
import ru.ssau.srestapp.exception.EntityType;
import ru.ssau.srestapp.repository.EventCategoryRepository;
import ru.ssau.srestapp.repository.UserInterestRepository;
import ru.ssau.srestapp.repository.UserRepository;
import ru.ssau.srestapp.util.SecurityUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserInterestService {

    private final UserInterestRepository userInterestRepository;
    private final UserRepository userRepository;
    private final EventCategoryRepository eventCategoryRepository;

    @Transactional(readOnly = true)
    public List<UserCategoryDto> getCategoriesForCurrentUser() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return userInterestRepository.findByUserId(currentUserId)
                .stream()
                .map(ui -> toCategoryDto(ui.getIdEventCategory()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserCategoryDto> getCategoriesByUserId(Long userId) throws EntityNotFoundException {
        findUserOrThrow(userId);
        return userInterestRepository.findByUserId(userId)
                .stream()
                .map(ui -> toCategoryDto(ui.getIdEventCategory()))
                .toList();
    }

    @Transactional
    public UserInterestResponseDto addInterestForCurrentUser(UserInterestRequestDto dto) throws EntityNotFoundException, DuplicateEntityException {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User user = findUserOrThrow(currentUserId);
        EventCategory category = findCategoryOrThrow(dto.getEventCategoryId());
        if (userInterestRepository.existsByIdUser_IdUserAndIdEventCategory_IdEventCategory(currentUserId, dto.getEventCategoryId())) {
            throw new DuplicateEntityException("Эта категория уже в ваших интересах");
        }
        UserInterest entity = new UserInterest();
        entity.setIdUser(user);
        entity.setIdEventCategory(category);
        return toResponseDto(userInterestRepository.save(entity));
    }

    @Transactional
    public void removeInterestForCurrentUser(Long categoryId) throws EntityNotFoundException {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        findCategoryOrThrow(categoryId);
        if (!userInterestRepository.existsByIdUser_IdUserAndIdEventCategory_IdEventCategory(
                currentUserId, categoryId)) {
            throw new EntityNotFoundException("Интерес не найден");
        }
        userInterestRepository.deleteByIdUser_IdUserAndIdEventCategory_IdEventCategory(currentUserId, categoryId);
    }

    @Transactional
    public List<UserCategoryDto> updateInterestsForCurrentUser(UserInterestsUpdateRequestDto dto) throws EntityNotFoundException {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        userInterestRepository.deleteAllByIdUser_IdUser(currentUserId);
        if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
            User user = findUserOrThrow(currentUserId);
            for (Long categoryId : dto.getCategoryIds()) {
                EventCategory category = findCategoryOrThrow(categoryId);
                UserInterest entity = new UserInterest();
                entity.setIdUser(user);
                entity.setIdEventCategory(category);
                userInterestRepository.save(entity);
            }
        }
        return getCategoriesForCurrentUser();
    }

    @Transactional
    public void deleteAllInterestsForCurrentUser() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        userInterestRepository.deleteAllByIdUser_IdUser(currentUserId);
    }

    private User findUserOrThrow(Long id) throws EntityNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(EntityType.USER.notFound(id)));
    }

    private EventCategory findCategoryOrThrow(Long id) throws EntityNotFoundException {
        return eventCategoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(EntityType.CATEGORY.notFoundFeminine(id)));
    }

    private UserCategoryDto toCategoryDto(EventCategory category) {
        return new UserCategoryDto(
                category.getIdEventCategory(),
                category.getEventCategoryName(),
                category.getColorCode()
        );
    }

    private UserInterestResponseDto toResponseDto(UserInterest ui) {
        return new UserInterestResponseDto(
                ui.getIdUser().getIdUser(),
                ui.getIdUser().getFio(),
                ui.getIdEventCategory().getIdEventCategory(),
                ui.getIdEventCategory().getEventCategoryName(),
                ui.getIdEventCategory().getColorCode()
        );
    }
}
