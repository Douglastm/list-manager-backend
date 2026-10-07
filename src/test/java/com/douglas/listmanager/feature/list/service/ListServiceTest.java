package com.douglas.listmanager.feature.list.service;

import com.douglas.listmanager.feature.list.dto.CreateListRequest;
import com.douglas.listmanager.feature.list.dto.ListResponse;
import com.douglas.listmanager.feature.list.dto.UpdateListRequest;
import com.douglas.listmanager.feature.list.entity.UserList;
import com.douglas.listmanager.feature.list.mapper.ListMapper;
import com.douglas.listmanager.feature.list.repository.ListRepository;
import com.douglas.listmanager.feature.user.entity.User;
import com.douglas.listmanager.feature.user.repository.UserRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListServiceTest {

    @Mock
    private ListRepository listRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ListMapper listMapper;

    @InjectMocks
    private ListService listService;

    @Test
    void shouldCreateList() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .name("Douglas")
                .email("douglas@email.com")
                .active(true)
                .build();

        CreateListRequest request =
                new CreateListRequest(
                        "Compras",
                        "Lista de compras"
                );

        UserList list = UserList.builder()
                .id(listId)
                .user(user)
                .name("Compras")
                .description("Lista de compras")
                .active(true)
                .build();

        ListResponse response =
                new ListResponse(
                        listId,
                        "Compras",
                        "Lista de compras",
                        true,
                        null,
                        null
                );

        when(userRepository.findByIdAndActiveTrue(userId))
                .thenReturn(Optional.of(user));

        when(listRepository.save(any(UserList.class)))
                .thenReturn(list);

        when(listMapper.toResponse(list))
                .thenReturn(response);

        ListResponse result =
                listService.create(userId, request);

        assertEquals(response, result);

        verify(userRepository)
                .findByIdAndActiveTrue(userId);

        verify(listRepository)
                .save(any(UserList.class));

        verify(listMapper)
                .toResponse(list);
    }

    @Test
    void shouldNotCreateListWhenUserDoesNotExist() {

        UUID userId = UUID.randomUUID();

        CreateListRequest request =
                new CreateListRequest(
                        "Compras",
                        "Lista de compras"
                );

        when(userRepository.findByIdAndActiveTrue(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                BusinessException.class,
                () -> listService.create(userId, request)
        );

        verify(listRepository, never())
                .save(any(UserList.class));
    }

    @Test
    void shouldFindAllUserLists() {

        UUID userId = UUID.randomUUID();

        UserList list1 = UserList.builder()
                .id(UUID.randomUUID())
                .name("Compras")
                .active(true)
                .build();

        UserList list2 = UserList.builder()
                .id(UUID.randomUUID())
                .name("Estudos")
                .active(true)
                .build();

        ListResponse response1 =
                new ListResponse(
                        list1.getId(),
                        "Compras",
                        null,
                        true,
                        null,
                        null
                );

        ListResponse response2 =
                new ListResponse(
                        list2.getId(),
                        "Estudos",
                        null,
                        true,
                        null,
                        null
                );

        when(listRepository.findAllByUserIdAndActiveTrue(userId))
                .thenReturn(List.of(list1, list2));

        when(listMapper.toResponse(list1))
                .thenReturn(response1);

        when(listMapper.toResponse(list2))
                .thenReturn(response2);

        List<ListResponse> result =
                listService.findAll(userId);

        assertEquals(2, result.size());
        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoLists() {

        UUID userId = UUID.randomUUID();

        when(listRepository.findAllByUserIdAndActiveTrue(userId))
                .thenReturn(List.of());

        List<ListResponse> result =
                listService.findAll(userId);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindListById() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();

        UserList list = UserList.builder()
                .id(listId)
                .name("Compras")
                .active(true)
                .build();

        ListResponse response =
                new ListResponse(
                        listId,
                        "Compras",
                        null,
                        true,
                        null,
                        null
                );

        when(
                listRepository
                        .findByIdAndUserIdAndActiveTrue(listId, userId)
        ).thenReturn(Optional.of(list));

        when(listMapper.toResponse(list))
                .thenReturn(response);

        ListResponse result =
                listService.findById(userId, listId);

        assertEquals(response, result);
    }

    @Test
    void shouldNotFindListFromAnotherUser() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();

        when(
                listRepository
                        .findByIdAndUserIdAndActiveTrue(listId, userId)
        ).thenReturn(Optional.empty());

        assertThrows(
                BusinessException.class,
                () -> listService.findById(userId, listId)
        );
    }

    @Test
    void shouldUpdateList() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();

        UserList list = UserList.builder()
                .id(listId)
                .name("Compras")
                .description("Antiga")
                .active(true)
                .build();

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

        when(
                listRepository
                        .findByIdAndUserIdAndActiveTrue(listId, userId)
        ).thenReturn(Optional.of(list));

        when(listRepository.save(list))
                .thenReturn(list);

        when(listMapper.toResponse(list))
                .thenReturn(response);

        ListResponse result =
                listService.update(
                        userId,
                        listId,
                        request
                );

        assertEquals(
                "Compras Atualizadas",
                list.getName()
        );

        assertEquals(
                "Nova descrição",
                list.getDescription()
        );

        assertEquals(response, result);
    }

    @Test
    void shouldNotUpdateListFromAnotherUser() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();

        UpdateListRequest request =
                new UpdateListRequest(
                        "Nova lista",
                        null
                );

        when(
                listRepository
                        .findByIdAndUserIdAndActiveTrue(listId, userId)
        ).thenReturn(Optional.empty());

        assertThrows(
                BusinessException.class,
                () -> listService.update(
                        userId,
                        listId,
                        request
                )
        );

        verify(listRepository, never())
                .save(any(UserList.class));
    }

    @Test
    void shouldDeleteList() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();

        UserList list = UserList.builder()
                .id(listId)
                .name("Compras")
                .active(true)
                .build();

        when(
                listRepository
                        .findByIdAndUserIdAndActiveTrue(listId, userId)
        ).thenReturn(Optional.of(list));

        listService.delete(userId, listId);

        assertFalse(list.getActive());

        verify(listRepository)
                .save(list);
    }

    @Test
    void shouldNotDeleteListFromAnotherUser() {

        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();

        when(
                listRepository
                        .findByIdAndUserIdAndActiveTrue(listId, userId)
        ).thenReturn(Optional.empty());

        assertThrows(
                BusinessException.class,
                () -> listService.delete(userId, listId)
        );

        verify(listRepository, never())
                .save(any(UserList.class));
    }
}