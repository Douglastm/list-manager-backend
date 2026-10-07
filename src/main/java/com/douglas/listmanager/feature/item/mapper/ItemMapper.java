package com.douglas.listmanager.feature.item.mapper;

import com.douglas.listmanager.feature.item.dto.ItemResponse;
import com.douglas.listmanager.feature.item.entity.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    public ItemResponse toResponse(Item item) {

        return new ItemResponse(
                item.getId(),
                item.getList().getId(),
                item.getName(),
                item.getDescription(),
                item.getCompleted(),
                item.getActive(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }
}