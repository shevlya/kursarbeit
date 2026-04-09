package ru.ssau.srestapp.exception;

public class InvalidPasswordException extends Exception {
    public InvalidPasswordException() {
        super("Неверный текущий пароль");
    }
}
