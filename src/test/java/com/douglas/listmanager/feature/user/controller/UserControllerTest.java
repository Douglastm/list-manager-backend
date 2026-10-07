package com.douglas.listmanager.feature.user.controller;

import com.douglas.listmanager.feature.user.dto.CreateUserRequest;
import com.douglas.listmanager.feature.user.dto.UpdateUserRequest;
import com.douglas.listmanager.feature.user.dto.UserResponse;
import com.douglas.listmanager.feature.user.service.UserService;
import com.douglas.listmanager.shared.exception.GlobalExceptionHandler;
import com.douglas.listmanager.shared.exception.ResourceNotFoundException;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void shouldCreateUser() throws Exception {

        UUID userId = UUID.randomUUID();

        CreateUserRequest request = new CreateUserRequest(
                "Douglas",
                "douglas@email.com",
                "123456"
        );

        UserResponse response = new UserResponse(
                userId,
                "Douglas",
                "douglas@email.com",
                true,
                LocalDateTime.now(),
                null
        );

        when(userService.create(any(CreateUserRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.name").value("Douglas"))
                .andExpect(jsonPath("$.email")
                        .value("douglas@email.com"))
                .andExpect(jsonPath("$.active").value(true));

        verify(userService)
                .create(any(CreateUserRequest.class));
    }

    @Test
    void shouldFindAllUsers() throws Exception {

        UUID firstId = UUID.randomUUID();
        UUID secondId = UUID.randomUUID();

        UserResponse firstUser = new UserResponse(
                firstId,
                "Douglas",
                "douglas@email.com",
                true,
                LocalDateTime.now(),
                null
        );

        UserResponse secondUser = new UserResponse(
                secondId,
                "João",
                "joao@email.com",
                true,
                LocalDateTime.now(),
                null
        );

        when(userService.findAll())
                .thenReturn(List.of(firstUser, secondUser));

        mockMvc.perform(
                        get("/api/v1/users")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name")
                        .value("Douglas"))
                .andExpect(jsonPath("$[1].name")
                        .value("João"));

        verify(userService)
                .findAll();
    }

    @Test
    void shouldFindUserById() throws Exception {

        UUID userId = UUID.randomUUID();

        UserResponse response = new UserResponse(
                userId,
                "Douglas",
                "douglas@email.com",
                true,
                LocalDateTime.now(),
                null
        );

        when(userService.findById(userId))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/v1/users/{id}", userId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(userId.toString()))
                .andExpect(jsonPath("$.name")
                        .value("Douglas"))
                .andExpect(jsonPath("$.email")
                        .value("douglas@email.com"));

        verify(userService)
                .findById(userId);
    }

    @Test
    void shouldReturn404WhenUserDoesNotExist() throws Exception {

        UUID userId = UUID.randomUUID();

        when(userService.findById(userId))
                .thenThrow(
                        new ResourceNotFoundException("User not found")
                );

        mockMvc.perform(
                        get("/api/v1/users/{id}", userId)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("User not found"));

        verify(userService)
                .findById(userId);
    }

    @Test
    void shouldUpdateUser() throws Exception {

        UUID userId = UUID.randomUUID();

        UpdateUserRequest request = new UpdateUserRequest(
                "Douglas Magalhães",
                "douglas.novo@email.com",
                "novaSenha123"
        );

        UserResponse response = new UserResponse(
                userId,
                "Douglas Magalhães",
                "douglas.novo@email.com",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(userService.update(
                eq(userId),
                any(UpdateUserRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put("/api/v1/users/{id}", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(userId.toString()))
                .andExpect(jsonPath("$.name")
                        .value("Douglas Magalhães"))
                .andExpect(jsonPath("$.email")
                        .value("douglas.novo@email.com"));

        verify(userService)
                .update(
                        eq(userId),
                        any(UpdateUserRequest.class)
                );
    }

    @Test
    void shouldDeactivateUser() throws Exception {

        UUID userId = UUID.randomUUID();

        doNothing()
                .when(userService)
                .deactivate(userId);

        mockMvc.perform(
                        delete("/api/v1/users/{id}", userId)
                )
                .andExpect(status().isNoContent());

        verify(userService)
                .deactivate(userId);
    }

    @Test
    void shouldReturn400WhenCreateRequestIsInvalid()
            throws Exception {

        CreateUserRequest request = new CreateUserRequest(
                "",
                "invalid-email",
                "123"
        );

        mockMvc.perform(
                        post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Validation error"));

        verify(userService, never())
                .create(any(CreateUserRequest.class));
    }
}