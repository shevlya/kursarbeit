package ru.ssau.srestapp.exception;

public class PasswordSameAsOldException extends Exception {
    public PasswordSameAsOldException() {
        super("Новый пароль не должен совпадать с текущим");
    }
}