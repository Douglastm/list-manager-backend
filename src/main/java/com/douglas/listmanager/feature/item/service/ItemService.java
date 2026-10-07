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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ListRepository listRepository;
    private final ItemMapper itemMapper;

    @Transactional
    public ItemResponse create(
            UUID userId,
            UUID listId,
            CreateItemRequest request
    ) {

        UserList list = findUserList(userId, listId);

        Item item = Item.builder()
                .list(list)
                .name(request.name())
                .description(request.description())
                .completed(false)
                .active(true)
                .build();

        Item savedItem = itemRepository.save(item);

        return itemMapper.toResponse(savedItem);
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> findAll(
            UUID userId,
            UUID listId
    ) {

        findUserList(userId, listId);

        return itemRepository
                .findAllByListIdAndListUserIdAndActiveTrue(
                        listId,
                        userId
                )
                .stream()
                .map(itemMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ItemResponse findById(
            UUID userId,
            UUID listId,
            UUID itemId
    ) {

        Item item = findItem(
                userId,
                listId,
                itemId
        );

        return itemMapper.toResponse(item);
    }

    @Transactional
    public ItemResponse update(
            UUID userId,
            UUID listId,
            UUID itemId,
            UpdateItemRequest request
    ) {

        Item item = findItem(
                userId,
                listId,
                itemId
        );

        item.setName(request.name());
        item.setDescription(request.description());

        Item updatedItem = itemRepository.save(item);

        return itemMapper.toResponse(updatedItem);
    }

    @Transactional
    public void delete(
            UUID userId,
            UUID listId,
            UUID itemId
    ) {

        Item item = findItem(
                userId,
                listId,
                itemId
        );

        item.setActive(false);

        itemRepository.save(item);
    }

    @Transactional
    public ItemResponse toggleCompleted(
            UUID userId,
            UUID listId,
            UUID itemId
    ) {

        Item item = findItem(
                userId,
                listId,
                itemId
        );

        item.setCompleted(!item.getCompleted());

        Item updatedItem = itemRepository.save(item);

        return itemMapper.toResponse(updatedItem);
    }

    private UserList findUserList(
            UUID userId,
            UUID listId
    ) {

        return listRepository
                .findByIdAndUserIdAndActiveTrue(
                        listId,
                        userId
                )
                .orElseThrow(() ->
                        new BusinessException("List not found")
                );
    }

    private Item findItem(
            UUID userId,
            UUID listId,
            UUID itemId
    ) {

        return itemRepository
                .findByIdAndListIdAndListUserIdAndActiveTrue(
                        itemId,
                        listId,
                        userId
                )
                .orElseThrow(() ->
                        new BusinessException("Item not found")
                );
    }
}