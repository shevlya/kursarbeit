package ru.ssau.srestapp.exception;

import lombok.Getter;

@Getter
public enum DateTimeErrorMessages {
    START_END_REQUIRED("Время начала и окончания обязательны"),
    START_MUST_BE_BEFORE_END("Время начала должно быть раньше времени окончания");

    private final String message;

    DateTimeErrorMessages(String message) {
        this.message = message;
    }
}
