package com.douglas.listmanager.shared.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BusinessExceptionTest {

    @Test
    void shouldCreateBusinessExceptionWithMessage() {

        BusinessException exception =
                new BusinessException("List not found");

        assertEquals(
                "List not found",
                exception.getMessage()
        );
    }
}