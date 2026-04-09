package ru.ssau.srestapp.exception;

public class EventNotEditableException extends Exception {
    public EventNotEditableException() {
        super("Нельзя редактировать уже начавшееся или завершённое мероприятие");
    }
}
