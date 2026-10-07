package com.douglas.listmanager.feature.list.mapper;

import com.douglas.listmanager.feature.list.dto.ListResponse;
import com.douglas.listmanager.feature.list.entity.UserList;
import org.springframework.stereotype.Component;

@Component
public class ListMapper {

    public ListResponse toResponse(UserList list) {

        return new ListResponse(
                list.getId(),
                list.getName(),
                list.getDescription(),
                list.getActive(),
                list.getCreatedAt(),
                list.getUpdatedAt()
        );
    }
}