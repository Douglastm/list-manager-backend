package com.douglas.listmanager.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);

        when(request.getRequestURI())
                .thenReturn("/api/v1/lists");
    }

    @Test
    void shouldHandleBusinessException() {

        BusinessException exception =
                new BusinessException("List not found");

        ResponseEntity<ErrorResponse> response =
                handler.handleBusinessException(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                404,
                response.getBody().status()
        );

        assertEquals(
                "Not Found",
                response.getBody().error()
        );

        assertEquals(
                "List not found",
                response.getBody().message()
        );

        assertEquals(
                "/api/v1/lists",
                response.getBody().path()
        );

        assertTrue(
                response.getBody().details().isEmpty()
        );
    }

    @Test
    void shouldHandleValidationException() {

        MethodArgumentNotValidException exception =
                mock(MethodArgumentNotValidException.class);

        FieldError fieldError =
                new FieldError(
                        "createListRequest",
                        "name",
                        "Name is required"
                );

        when(exception.getBindingResult())
                .thenReturn(
                        new org.springframework.validation
                                .BeanPropertyBindingResult(
                                new Object(),
                                "createListRequest"
                        )
                );

        exception
                .getBindingResult()
                .addError(fieldError);

        ResponseEntity<ErrorResponse> response =
                handler.handleValidationException(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                400,
                response.getBody().status()
        );

        assertEquals(
                "Validation failed",
                response.getBody().message()
        );

        assertEquals(
                List.of("name: Name is required"),
                response.getBody().details()
        );
    }

    @Test
    void shouldHandleInvalidJson() {

        org.springframework.http.converter
                .HttpMessageNotReadableException exception =
                mock(
                        org.springframework.http.converter
                                .HttpMessageNotReadableException.class
                );

        ResponseEntity<ErrorResponse> response =
                handler.handleInvalidJson(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                400,
                response.getBody().status()
        );

        assertEquals(
                "Invalid request body",
                response.getBody().message()
        );
    }

    @Test
    void shouldHandleUnexpectedException() {

        Exception exception =
                new RuntimeException("Database failure");

        ResponseEntity<ErrorResponse> response =
                handler.handleUnexpectedException(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                500,
                response.getBody().status()
        );

        assertEquals(
                "Internal Server Error",
                response.getBody().error()
        );

        assertEquals(
                "An unexpected error occurred",
                response.getBody().message()
        );
    }
}