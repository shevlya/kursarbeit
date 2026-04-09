package ru.ssau.srestapp.exception;

public class ModerationException extends Exception {
    public ModerationException() {
        super("Нет ожидаемых изменений для этого мероприятия");
    }
}
