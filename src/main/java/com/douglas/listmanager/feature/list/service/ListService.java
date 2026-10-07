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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListService {

    private final ListRepository listRepository;
    private final UserRepository userRepository;
    private final ListMapper listMapper;

    @Transactional
    public ListResponse create(
            UUID userId,
            CreateListRequest request
    ) {

        User user = userRepository
                .findByIdAndActiveTrue(userId)
                .orElseThrow(() ->
                        new BusinessException("User not found")
                );

        UserList list = UserList.builder()
                .user(user)
                .name(request.name())
                .description(request.description())
                .active(true)
                .build();

        UserList savedList = listRepository.save(list);

        return listMapper.toResponse(savedList);
    }

    @Transactional(readOnly = true)
    public List<ListResponse> findAll(UUID userId) {

        return listRepository
                .findAllByUserIdAndActiveTrue(userId)
                .stream()
                .map(listMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ListResponse findById(
            UUID userId,
            UUID listId
    ) {

        UserList list = listRepository
                .findByIdAndUserIdAndActiveTrue(listId, userId)
                .orElseThrow(() ->
                        new BusinessException("List not found")
                );

        return listMapper.toResponse(list);
    }

    @Transactional
    public ListResponse update(
            UUID userId,
            UUID listId,
            UpdateListRequest request
    ) {

        UserList list = listRepository
                .findByIdAndUserIdAndActiveTrue(listId, userId)
                .orElseThrow(() ->
                        new BusinessException("List not found")
                );

        list.setName(request.name());
        list.setDescription(request.description());

        UserList updatedList = listRepository.save(list);

        return listMapper.toResponse(updatedList);
    }

    @Transactional
    public void delete(
            UUID userId,
            UUID listId
    ) {

        UserList list = listRepository
                .findByIdAndUserIdAndActiveTrue(listId, userId)
                .orElseThrow(() ->
                        new BusinessException("List not found")
                );

        list.setActive(false);

        listRepository.save(list);
    }
}