package ru.ssau.srestapp.controller;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.ssau.srestapp.exception.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> buildBody(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return body;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.debug("Ошибка валидации: {}", message);
        return buildBody(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleNotReadable(HttpMessageNotReadableException ex) {
        log.debug("Некорректный JSON: {}", ex.getMessage());
        return buildBody(HttpStatus.BAD_REQUEST, "Некорректный формат запроса");
    }

    @ExceptionHandler({
            EmailAlreadyExistsException.class,
            DuplicateEntityException.class,
            ParticipantAlreadyExistsException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleConflict(Exception ex) {
        log.debug("Конфликт: {}", ex.getMessage());
        return buildBody(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler({EntityNotFoundException.class, ParticipantNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(Exception ex) {
        log.debug("Не найдено: {}", ex.getMessage());
        return buildBody(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, Object> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Доступ запрещён: {}", ex.getMessage());
        return buildBody(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler({BadCredentialsException.class, JwtException.class, AuthenticationException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> handleUnauthorized(Exception ex) {
        String message = ex instanceof AuthenticationException ? "Ошибка аутентификации" : ex.getMessage();
        log.warn("Ошибка аутентификации: {}", message);
        return buildBody(HttpStatus.UNAUTHORIZED, message);
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            InvalidDateTimeException.class,
            EventMaxParticipantsReachedException.class,
            ModerationException.class,
            EventNotEditableException.class,
            IllegalStateException.class,
            InvalidPasswordException.class,
            PasswordSameAsOldException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleBadRequest(Exception ex) {
        log.debug("Ошибка запроса: {}", ex.getMessage());
        return buildBody(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleAll(Exception ex) {
        log.error("Необработанное исключение: ", ex);
        return buildBody(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
    }
}
