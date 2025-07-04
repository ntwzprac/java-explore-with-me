package ru.practicum.mainservice.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.mainservice.dto.response.ApiError;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExceptionControllerTest {
    private final ExceptionController controller = new ExceptionController();

    @Test
    void handleConflictException() {
        ConflictException ex = new ConflictException("conflict");
        ResponseEntity<ApiError> response = controller.handleConflict(ex);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("conflict", response.getBody().getMessage());
    }

    @Test
    void handleNotFoundException() {
        NotFoundException ex = new NotFoundException("not found");
        ResponseEntity<ApiError> response = controller.handleNotFound(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("not found", response.getBody().getMessage());
    }

    @Test
    void handleInvalidDateException() {
        InvalidDateException ex = new InvalidDateException("invalid date");
        ResponseEntity<ApiError> response = controller.handleInvalidDate(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("invalid date", response.getBody().getMessage());
    }

    @Test
    void handleEventConflictException() {
        EventConflictException ex = new EventConflictException("event conflict");
        ResponseEntity<ApiError> response = controller.handleEventConflict(ex);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("event conflict", response.getBody().getMessage());
    }
}