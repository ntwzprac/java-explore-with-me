package ru.practicum.mainservice.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.mainservice.dto.response.ApiError;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@RestControllerAdvice
public class ExceptionController {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Logger log = LoggerFactory.getLogger(ExceptionController.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        ApiError error = ApiError.builder()
                .errors(Collections.singletonList(e.toString()))
                .message(message)
                .reason("Incorrectly made request.")
                .status(HttpStatus.BAD_REQUEST.name())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        String message = String.format("Required request parameter '%s' is not present", e.getParameterName());
        ApiError error = ApiError.builder()
                .errors(Collections.singletonList(e.toString()))
                .message(message)
                .reason("Incorrectly made request.")
                .status(HttpStatus.BAD_REQUEST.name())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException e) {
        ApiError error = ApiError.builder()
                .errors(Collections.singletonList(e.toString()))
                .message(e.getMessage())
                .reason("The required object was not found.")
                .status(HttpStatus.NOT_FOUND.name())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        ApiError error = ApiError.builder()
                .errors(Collections.singletonList(e.toString()))
                .message("Malformed JSON or request body: " + e.getMessage())
                .reason("Incorrectly made request.")
                .status(HttpStatus.BAD_REQUEST.name())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .findFirst().orElse("Validation failed");
        ApiError error = ApiError.builder()
                .errors(Collections.singletonList(e.toString()))
                .message(message)
                .reason("Incorrectly made request.")
                .status(HttpStatus.BAD_REQUEST.name())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException e) {
        ApiError error = ApiError.builder()
                .errors(Collections.singletonList(e.toString()))
                .message(e.getMessage())
                .reason("Conflict or integrity constraint.")
                .status(HttpStatus.CONFLICT.name())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOther(Exception e) {
        log.error("Unexpected error", e);
        ApiError error = ApiError.builder()
                .errors(Collections.singletonList(e.toString()))
                .message(e.getMessage())
                .reason("Unexpected error.")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<ApiError> handleInvalidDate(InvalidDateException e) {
        ApiError error = ApiError.builder()
                .errors(Collections.singletonList(e.toString()))
                .message(e.getMessage())
                .reason("Invalid date format.")
                .status(HttpStatus.BAD_REQUEST.name())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(EventConflictException.class)
    public ResponseEntity<ApiError> handleEventConflict(EventConflictException e) {
        ApiError error = ApiError.builder()
                .errors(Collections.singletonList(e.toString()))
                .message(e.getMessage())
                .reason("Event conflict.")
                .status(HttpStatus.CONFLICT.name())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(CommentMissingPermissionException.class)
    public ResponseEntity<ApiError> handleCommentMissingPermission(CommentMissingPermissionException e) {
        ApiError error = ApiError.builder()
                .errors(Collections.singletonList(e.toString()))
                .message(e.getMessage())
                .reason("User does not have permission to perform this action on the comment.")
                .status(HttpStatus.FORBIDDEN.name())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(WrongCommentEventIdException.class)
    public ResponseEntity<ApiError> handleWrongCommentEventId(WrongCommentEventIdException e) {
        ApiError error = ApiError.builder()
                .errors(Collections.singletonList(e.toString()))
                .message(e.getMessage())
                .reason("The event ID does not match the comment's event.")
                .status(HttpStatus.BAD_REQUEST.name())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
        return ResponseEntity.badRequest().body(error);
    }
}