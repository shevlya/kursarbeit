package ru.ssau.srestapp.exception;

public class ParticipantNotFoundException extends Exception {
    public ParticipantNotFoundException(Long userId, Long eventId) {
        super("Участник с userId=" + userId + " не найден на мероприятии с eventId=" + eventId);
    }
}
