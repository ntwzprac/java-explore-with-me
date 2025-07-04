package ru.practicum.mainservice.exception;

public class WrongCommentEventIdException extends RuntimeException {
    public WrongCommentEventIdException(String message) {
        super(message);
    }
}
