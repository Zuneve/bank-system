package ru.gusev.exception;

public class RateServiceUnavailableException extends RuntimeException {
    public RateServiceUnavailableException(String message) {
        super(message);
    }

    public RateServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
