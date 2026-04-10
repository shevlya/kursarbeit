package ru.ssau.srestapp.util;

import org.springframework.security.core.context.SecurityContextHolder;
import ru.ssau.srestapp.exception.UtilException;
import ru.ssau.srestapp.security.CustomUserDetails;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static CustomUserDetails getCurrentUserDetails() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UtilException("Пользователь не аутентифицирован");
        }
        return (CustomUserDetails) authentication.getPrincipal();
    }

    public static Long getCurrentUserId() {
        return getCurrentUserDetails().getUserId();
    }
}
