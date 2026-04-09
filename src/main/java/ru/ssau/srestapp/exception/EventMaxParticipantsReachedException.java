package ru.ssau.srestapp.exception;

public class EventMaxParticipantsReachedException extends Exception {
    public EventMaxParticipantsReachedException(Long eventId) {
        super("Достигнуто максимальное количество участников для мероприятия с id=" + eventId);
    }
}
