package com.douglas.listmanager.feature.item.controller;

import com.douglas.listmanager.feature.item.dto.CreateItemRequest;
import com.douglas.listmanager.feature.item.dto.ItemResponse;
import com.douglas.listmanager.feature.item.dto.UpdateItemRequest;
import com.douglas.listmanager.feature.item.service.ItemService;
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
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ItemController itemController;

    @Test
    void shouldCreateItem() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        CreateItemRequest request =
                new CreateItemRequest(
                        "Comprar café",
                        "Café 500g"
                );

        ItemResponse response =
                new ItemResponse(
                        itemId,
                        listId,
                        "Comprar café",
                        "Café 500g",
                        false,
                        true,
                        null,
                        null
                );

        when(authentication.getPrincipal())
                .thenReturn(userId);

        when(
                itemService.create(
                        userId,
                        listId,
                        request
                )
        ).thenReturn(response);

        ResponseEntity<ItemResponse> result =
                itemController.create(
                        listId,
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

        verify(itemService)
                .create(
                        userId,
                        listId,
                        request
                );
    }

    @Test
    void shouldFindAllItems() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();

        List<ItemResponse> response =
                List.of(
                        new ItemResponse(
                                UUID.randomUUID(),
                                listId,
                                "Arroz",
                                null,
                                false,
                                true,
                                null,
                                null
                        )
                );

        when(authentication.getPrincipal())
                .thenReturn(userId);

        when(
                itemService.findAll(
                        userId,
                        listId
                )
        ).thenReturn(response);

        ResponseEntity<List<ItemResponse>> result =
                itemController.findAll(
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
    void shouldFindItemById() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        ItemResponse response =
                new ItemResponse(
                        itemId,
                        listId,
                        "Arroz",
                        null,
                        false,
                        true,
                        null,
                        null
                );

        when(authentication.getPrincipal())
                .thenReturn(userId);

        when(
                itemService.findById(
                        userId,
                        listId,
                        itemId
                )
        ).thenReturn(response);

        ResponseEntity<ItemResponse> result =
                itemController.findById(
                        listId,
                        itemId,
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
    void shouldUpdateItem() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        UpdateItemRequest request =
                new UpdateItemRequest(
                        "Arroz integral",
                        "Arroz integral 1kg"
                );

        ItemResponse response =
                new ItemResponse(
                        itemId,
                        listId,
                        "Arroz integral",
                        "Arroz integral 1kg",
                        false,
                        true,
                        null,
                        null
                );

        when(authentication.getPrincipal())
                .thenReturn(userId);

        when(
                itemService.update(
                        userId,
                        listId,
                        itemId,
                        request
                )
        ).thenReturn(response);

        ResponseEntity<ItemResponse> result =
                itemController.update(
                        listId,
                        itemId,
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
    void shouldToggleItemCompleted() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        ItemResponse response =
                new ItemResponse(
                        itemId,
                        listId,
                        "Arroz",
                        null,
                        true,
                        true,
                        null,
                        null
                );

        when(authentication.getPrincipal())
                .thenReturn(userId);

        when(
                itemService.toggleCompleted(
                        userId,
                        listId,
                        itemId
                )
        ).thenReturn(response);

        ResponseEntity<ItemResponse> result =
                itemController.toggleCompleted(
                        listId,
                        itemId,
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
    void shouldDeleteItem() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        when(authentication.getPrincipal())
                .thenReturn(userId);

        ResponseEntity<Void> result =
                itemController.delete(
                        listId,
                        itemId,
                        authentication
                );

        assertEquals(
                HttpStatus.NO_CONTENT,
                result.getStatusCode()
        );

        verify(itemService)
                .delete(
                        userId,
                        listId,
                        itemId
                );
    }
}