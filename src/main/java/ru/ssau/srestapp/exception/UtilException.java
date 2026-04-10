package ru.ssau.srestapp.exception;

public class UtilException extends RuntimeException {

    public UtilException(String message) {
        super(message);
    }

    public UtilException(String message, Throwable cause) {
        super(message, cause);
    }
}
