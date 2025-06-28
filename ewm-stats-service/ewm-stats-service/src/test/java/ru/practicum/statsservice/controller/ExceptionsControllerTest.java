package ru.practicum.statsservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.statsdto.ErrorResponse;
import ru.practicum.statsservice.exception.BadRequestException;
import ru.practicum.statsservice.exception.StatsServiceException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExceptionsControllerTest {

    @InjectMocks
    private ExceptionsController exceptionsController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(exceptionsController)
                .setControllerAdvice(exceptionsController)
                .build();
    }

    @Test
    void handleBadRequest_ShouldReturnBadRequestStatus() {
        BadRequestException exception = new BadRequestException("Test bad request error");

        ResponseEntity<ErrorResponse> response = 
                exceptionsController.handleBadRequest(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Test bad request error", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
    }

    @Test
    void handleServiceException_ShouldReturnInternalServerErrorStatus() {
        StatsServiceException exception = new StatsServiceException("Test service error");

        ResponseEntity<ErrorResponse> response = 
                exceptionsController.handleServiceException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Test service error", response.getBody().getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().getStatus());
    }

    @Test
    void handleOther_ShouldReturnInternalServerErrorStatus() {
        RuntimeException exception = new RuntimeException("Test generic error");

        ResponseEntity<ErrorResponse> response = 
                exceptionsController.handleOther(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Test generic error", response.getBody().getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().getStatus());
    }

    @Test
    void handleValidationException_ShouldReturnBadRequestStatus() {
        org.springframework.web.bind.MethodArgumentNotValidException exception = 
                mock(org.springframework.web.bind.MethodArgumentNotValidException.class);
        org.springframework.validation.BindingResult bindingResult = 
                mock(org.springframework.validation.BindingResult.class);
        org.springframework.validation.FieldError fieldError = 
                mock(org.springframework.validation.FieldError.class);

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.Arrays.asList(fieldError));
        when(fieldError.getField()).thenReturn("testField");
        when(fieldError.getDefaultMessage()).thenReturn("Test validation error");

        ResponseEntity<ErrorResponse> response = 
                exceptionsController.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("testField: Test validation error", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
    }

    @Test
    void handleMissingServletRequestParameterException_ShouldReturnBadRequestStatus() {
        org.springframework.web.bind.MissingServletRequestParameterException exception = 
                new org.springframework.web.bind.MissingServletRequestParameterException("testParam", "String");

        ResponseEntity<ErrorResponse> response = 
                exceptionsController.handleMissingServletRequestParameterException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Required request parameter 'testParam' is not present", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
    }
} 