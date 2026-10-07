package com.douglas.listmanager.feature.item.service;

import com.douglas.listmanager.feature.item.dto.CreateItemRequest;
import com.douglas.listmanager.feature.item.dto.ItemResponse;
import com.douglas.listmanager.feature.item.dto.UpdateItemRequest;
import com.douglas.listmanager.feature.item.entity.Item;
import com.douglas.listmanager.feature.item.mapper.ItemMapper;
import com.douglas.listmanager.feature.item.repository.ItemRepository;
import com.douglas.listmanager.feature.list.entity.UserList;
import com.douglas.listmanager.feature.list.repository.ListRepository;
import com.douglas.listmanager.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ListRepository listRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemService itemService;

    @Test
    void shouldCreateItem() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        UserList list = UserList.builder()
                .id(listId)
                .name("Compras")
                .active(true)
                .build();

        CreateItemRequest request =
                new CreateItemRequest(
                        "Comprar café",
                        "Café 500g"
                );

        Item item = Item.builder()
                .id(itemId)
                .list(list)
                .name("Comprar café")
                .description("Café 500g")
                .completed(false)
                .active(true)
                .build();

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

        when(
                listRepository
                        .findByIdAndUserIdAndActiveTrue(
                                listId,
                                userId
                        )
        ).thenReturn(Optional.of(list));

        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        when(itemMapper.toResponse(item))
                .thenReturn(response);

        ItemResponse result =
                itemService.create(
                        userId,
                        listId,
                        request
                );

        assertEquals(response, result);

        verify(listRepository)
                .findByIdAndUserIdAndActiveTrue(
                        listId,
                        userId
                );

        verify(itemRepository)
                .save(any(Item.class));

        verify(itemMapper)
                .toResponse(item);
    }

    @Test
    void shouldNotCreateItemWhenListDoesNotBelongToUser() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();

        CreateItemRequest request =
                new CreateItemRequest(
                        "Comprar café",
                        null
                );

        when(
                listRepository
                        .findByIdAndUserIdAndActiveTrue(
                                listId,
                                userId
                        )
        ).thenReturn(Optional.empty());

        assertThrows(
                BusinessException.class,
                () -> itemService.create(
                        userId,
                        listId,
                        request
                )
        );

        verify(itemRepository, never())
                .save(any(Item.class));
    }

    @Test
    void shouldFindAllItems() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();

        UserList list = UserList.builder()
                .id(listId)
                .name("Compras")
                .active(true)
                .build();

        Item item1 = Item.builder()
                .id(UUID.randomUUID())
                .list(list)
                .name("Arroz")
                .active(true)
                .completed(false)
                .build();

        Item item2 = Item.builder()
                .id(UUID.randomUUID())
                .list(list)
                .name("Feijão")
                .active(true)
                .completed(false)
                .build();

        ItemResponse response1 =
                new ItemResponse(
                        item1.getId(),
                        listId,
                        "Arroz",
                        null,
                        false,
                        true,
                        null,
                        null
                );

        ItemResponse response2 =
                new ItemResponse(
                        item2.getId(),
                        listId,
                        "Feijão",
                        null,
                        false,
                        true,
                        null,
                        null
                );

        when(
                listRepository
                        .findByIdAndUserIdAndActiveTrue(
                                listId,
                                userId
                        )
        ).thenReturn(Optional.of(list));

        when(
                itemRepository
                        .findAllByListIdAndListUserIdAndActiveTrue(
                                listId,
                                userId
                        )
        ).thenReturn(List.of(item1, item2));

        when(itemMapper.toResponse(item1))
                .thenReturn(response1);

        when(itemMapper.toResponse(item2))
                .thenReturn(response2);

        List<ItemResponse> result =
                itemService.findAll(
                        userId,
                        listId
                );

        assertEquals(2, result.size());
        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));
    }

    @Test
    void shouldReturnEmptyListWhenListHasNoItems() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();

        UserList list = UserList.builder()
                .id(listId)
                .name("Compras")
                .active(true)
                .build();

        when(
                listRepository
                        .findByIdAndUserIdAndActiveTrue(
                                listId,
                                userId
                        )
        ).thenReturn(Optional.of(list));

        when(
                itemRepository
                        .findAllByListIdAndListUserIdAndActiveTrue(
                                listId,
                                userId
                        )
        ).thenReturn(List.of());

        List<ItemResponse> result =
                itemService.findAll(
                        userId,
                        listId
                );

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldNotFindItemsWhenListDoesNotBelongToUser() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();

        when(
                listRepository
                        .findByIdAndUserIdAndActiveTrue(
                                listId,
                                userId
                        )
        ).thenReturn(Optional.empty());

        assertThrows(
                BusinessException.class,
                () -> itemService.findAll(
                        userId,
                        listId
                )
        );

        verify(itemRepository, never())
                .findAllByListIdAndListUserIdAndActiveTrue(
                        any(),
                        any()
                );
    }

    @Test
    void shouldFindItemById() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        UserList list = UserList.builder()
                .id(listId)
                .build();

        Item item = Item.builder()
                .id(itemId)
                .list(list)
                .name("Arroz")
                .active(true)
                .completed(false)
                .build();

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

        when(
                itemRepository
                        .findByIdAndListIdAndListUserIdAndActiveTrue(
                                itemId,
                                listId,
                                userId
                        )
        ).thenReturn(Optional.of(item));

        when(itemMapper.toResponse(item))
                .thenReturn(response);

        ItemResponse result =
                itemService.findById(
                        userId,
                        listId,
                        itemId
                );

        assertEquals(response, result);
    }

    @Test
    void shouldNotFindItemFromAnotherUser() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        when(
                itemRepository
                        .findByIdAndListIdAndListUserIdAndActiveTrue(
                                itemId,
                                listId,
                                userId
                        )
        ).thenReturn(Optional.empty());

        assertThrows(
                BusinessException.class,
                () -> itemService.findById(
                        userId,
                        listId,
                        itemId
                )
        );
    }

    @Test
    void shouldUpdateItem() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        UserList list = UserList.builder()
                .id(listId)
                .build();

        Item item = Item.builder()
                .id(itemId)
                .list(list)
                .name("Arroz")
                .description("Arroz antigo")
                .active(true)
                .completed(false)
                .build();

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

        when(
                itemRepository
                        .findByIdAndListIdAndListUserIdAndActiveTrue(
                                itemId,
                                listId,
                                userId
                        )
        ).thenReturn(Optional.of(item));

        when(itemRepository.save(item))
                .thenReturn(item);

        when(itemMapper.toResponse(item))
                .thenReturn(response);

        ItemResponse result =
                itemService.update(
                        userId,
                        listId,
                        itemId,
                        request
                );

        assertEquals(
                "Arroz integral",
                item.getName()
        );

        assertEquals(
                "Arroz integral 1kg",
                item.getDescription()
        );

        assertEquals(response, result);
    }

    @Test
    void shouldNotUpdateItemFromAnotherUser() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        UpdateItemRequest request =
                new UpdateItemRequest(
                        "HACK",
                        null
                );

        when(
                itemRepository
                        .findByIdAndListIdAndListUserIdAndActiveTrue(
                                itemId,
                                listId,
                                userId
                        )
        ).thenReturn(Optional.empty());

        assertThrows(
                BusinessException.class,
                () -> itemService.update(
                        userId,
                        listId,
                        itemId,
                        request
                )
        );

        verify(itemRepository, never())
                .save(any(Item.class));
    }

    @Test
    void shouldDeleteItem() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        Item item = Item.builder()
                .id(itemId)
                .active(true)
                .completed(false)
                .build();

        when(
                itemRepository
                        .findByIdAndListIdAndListUserIdAndActiveTrue(
                                itemId,
                                listId,
                                userId
                        )
        ).thenReturn(Optional.of(item));

        itemService.delete(
                userId,
                listId,
                itemId
        );

        assertFalse(item.getActive());

        verify(itemRepository)
                .save(item);
    }

    @Test
    void shouldNotDeleteItemFromAnotherUser() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        when(
                itemRepository
                        .findByIdAndListIdAndListUserIdAndActiveTrue(
                                itemId,
                                listId,
                                userId
                        )
        ).thenReturn(Optional.empty());

        assertThrows(
                BusinessException.class,
                () -> itemService.delete(
                        userId,
                        listId,
                        itemId
                )
        );

        verify(itemRepository, never())
                .save(any(Item.class));
    }

    @Test
    void shouldToggleItemToCompleted() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        Item item = Item.builder()
                .id(itemId)
                .completed(false)
                .active(true)
                .build();

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

        when(
                itemRepository
                        .findByIdAndListIdAndListUserIdAndActiveTrue(
                                itemId,
                                listId,
                                userId
                        )
        ).thenReturn(Optional.of(item));

        when(itemRepository.save(item))
                .thenReturn(item);

        when(itemMapper.toResponse(item))
                .thenReturn(response);

        ItemResponse result =
                itemService.toggleCompleted(
                        userId,
                        listId,
                        itemId
                );

        assertTrue(item.getCompleted());
        assertEquals(response, result);
    }

    @Test
    void shouldToggleItemBackToIncomplete() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        Item item = Item.builder()
                .id(itemId)
                .completed(true)
                .active(true)
                .build();

        when(
                itemRepository
                        .findByIdAndListIdAndListUserIdAndActiveTrue(
                                itemId,
                                listId,
                                userId
                        )
        ).thenReturn(Optional.of(item));

        when(itemRepository.save(item))
                .thenReturn(item);

        itemService.toggleCompleted(
                userId,
                listId,
                itemId
        );

        assertFalse(item.getCompleted());

        verify(itemRepository)
                .save(item);
    }
}