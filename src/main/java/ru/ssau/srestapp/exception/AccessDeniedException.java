package ru.ssau.srestapp.exception;

public class AccessDeniedException extends Exception {
    public AccessDeniedException() {
        super("Доступ запрещён: вы можете редактировать только свой профиль");
    }
}