package ru.ssau.srestapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.ssau.srestapp.dto.event.EventShortDto;
import ru.ssau.srestapp.security.CustomUserDetails;
import ru.ssau.srestapp.service.RecommendationService;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/events")
    public List<EventShortDto> getRecommendedEvents(Authentication authentication, @RequestParam(defaultValue = "6") int limit) {
        Long userId = null;
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            userId = userDetails.getUserId();
        }
        return recommendationService.getRecommendedEvents(userId, limit);
    }
}
