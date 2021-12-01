package ru.test.exception;

public class CustomException extends RuntimeException {
    public CustomException(Exception e) {
        super(e);
    }
    public CustomException(String message) {
        super(message);
    }
}
