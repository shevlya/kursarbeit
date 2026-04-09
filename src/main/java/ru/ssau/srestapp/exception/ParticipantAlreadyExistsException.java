package ru.ssau.srestapp.exception;

public class ParticipantAlreadyExistsException extends Exception {
    public ParticipantAlreadyExistsException(Long userId, Long eventId) {
        super("Пользователь с id=" + userId + " уже записан на мероприятие с id=" + eventId);
    }
}
