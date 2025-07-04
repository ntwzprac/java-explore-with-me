package ru.practicum.mainservice.exception;

public class CommentMissingPermissionException extends RuntimeException {
    public CommentMissingPermissionException(String message) {
        super(message);
    }
}
