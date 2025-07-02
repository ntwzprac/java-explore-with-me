package ru.practicum.mainservice.dto.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Collections;

class ApiErrorTest {
    @Test
    void testApiErrorFields() {
        ApiError error = new ApiError();
        error.setMessage("Test message");
        error.setReason("Test reason");
        error.setStatus("BAD_REQUEST");
        error.setTimestamp("2024-01-01T00:00:00");

        assertEquals("Test message", error.getMessage());
        assertEquals("Test reason", error.getReason());
        assertEquals("BAD_REQUEST", error.getStatus());
        assertEquals("2024-01-01T00:00:00", error.getTimestamp());
    }

    @Test
    void testApiErrorConstructor() {
        ApiError error = new ApiError(Collections.singletonList("err1"), "message", "reason", "status", "2024-01-01T00:00:00");
        assertEquals(Collections.singletonList("err1"), error.getErrors());
        assertEquals("message", error.getMessage());
        assertEquals("reason", error.getReason());
        assertEquals("status", error.getStatus());
        assertEquals("2024-01-01T00:00:00", error.getTimestamp());
    }
}