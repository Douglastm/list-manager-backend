package com.douglas.listmanager.feature.list.controller;

import com.douglas.listmanager.feature.list.dto.CreateListRequest;
import com.douglas.listmanager.feature.list.dto.ListResponse;
import com.douglas.listmanager.feature.list.dto.UpdateListRequest;
import com.douglas.listmanager.feature.list.service.ListService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListControllerTest {

    @Mock
    private ListService listService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ListController listController;

    @Test
    void shouldCreateList() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();

        CreateListRequest request =
                new CreateListRequest(
                        "Compras",
                        "Lista de compras"
                );

        ListResponse response =
                new ListResponse(
                        listId,
                        "Compras",
                        "Lista de compras",
                        true,
                        null,
                        null
                );

        when(authentication.getPrincipal())
                .thenReturn(userId);

        when(listService.create(userId, request))
                .thenReturn(response);

        ResponseEntity<ListResponse> result =
                listController.create(
                        request,
                        authentication
                );

        assertEquals(
                HttpStatus.CREATED,
                result.getStatusCode()
        );

        assertEquals(
                response,
                result.getBody()
        );

        verify(listService)
                .create(userId, request);
    }

    @Test
    void shouldFindAllLists() {

        UUID userId = UUID.randomUUID();

        List<ListResponse> response =
                List.of(
                        new ListResponse(
                                UUID.randomUUID(),
                                "Compras",
                                null,
                                true,
                                null,
                                null
                        )
                );

        when(authentication.getPrincipal())
                .thenReturn(userId);

        when(listService.findAll(userId))
                .thenReturn(response);

        ResponseEntity<List<ListResponse>> result =
                listController.findAll(authentication);

        assertEquals(
                HttpStatus.OK,
                result.getStatusCode()
        );

        assertEquals(
                response,
                result.getBody()
        );
    }

    @Test
    void shouldFindListById() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();

        ListResponse response =
                new ListResponse(
                        listId,
                        "Compras",
                        null,
                        true,
                        null,
                        null
                );

        when(authentication.getPrincipal())
                .thenReturn(userId);

        when(listService.findById(userId, listId))
                .thenReturn(response);

        ResponseEntity<ListResponse> result =
                listController.findById(
                        listId,
                        authentication
                );

        assertEquals(
                HttpStatus.OK,
                result.getStatusCode()
        );

        assertEquals(
                response,
                result.getBody()
        );
    }

    @Test
    void shouldUpdateList() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();

        UpdateListRequest request =
                new UpdateListRequest(
                        "Compras Atualizadas",
                        "Nova descrição"
                );

        ListResponse response =
                new ListResponse(
                        listId,
                        "Compras Atualizadas",
                        "Nova descrição",
                        true,
                        null,
                        null
                );

        when(authentication.getPrincipal())
                .thenReturn(userId);

        when(
                listService.update(
                        userId,
                        listId,
                        request
                )
        ).thenReturn(response);

        ResponseEntity<ListResponse> result =
                listController.update(
                        listId,
                        request,
                        authentication
                );

        assertEquals(
                HttpStatus.OK,
                result.getStatusCode()
        );

        assertEquals(
                response,
                result.getBody()
        );
    }

    @Test
    void shouldDeleteList() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();

        when(authentication.getPrincipal())
                .thenReturn(userId);

        ResponseEntity<Void> result =
                listController.delete(
                        listId,
                        authentication
                );

        assertEquals(
                HttpStatus.NO_CONTENT,
                result.getStatusCode()
        );

        verify(listService)
                .delete(userId, listId);
    }
}