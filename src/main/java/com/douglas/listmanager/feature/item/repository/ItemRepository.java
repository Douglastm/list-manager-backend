package com.douglas.listmanager.feature.item.repository;

import com.douglas.listmanager.feature.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {

    List<Item> findAllByListIdAndListUserIdAndActiveTrue(
            UUID listId,
            UUID userId
    );

    Optional<Item> findByIdAndListIdAndListUserIdAndActiveTrue(
            UUID itemId,
            UUID listId,
            UUID userId
    );
}