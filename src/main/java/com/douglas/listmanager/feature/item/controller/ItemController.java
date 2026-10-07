package com.douglas.listmanager.feature.item.controller;

import com.douglas.listmanager.feature.item.dto.CreateItemRequest;
import com.douglas.listmanager.feature.item.dto.ItemResponse;
import com.douglas.listmanager.feature.item.dto.UpdateItemRequest;
import com.douglas.listmanager.feature.item.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lists/{listId}/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemResponse> create(
            @PathVariable UUID listId,
            @Valid @RequestBody CreateItemRequest request,
            Authentication authentication
    ) {

        UUID userId = (UUID) authentication.getPrincipal();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        itemService.create(
                                userId,
                                listId,
                                request
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<ItemResponse>> findAll(
            @PathVariable UUID listId,
            Authentication authentication
    ) {

        UUID userId = (UUID) authentication.getPrincipal();

        return ResponseEntity.ok(
                itemService.findAll(
                        userId,
                        listId
                )
        );
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponse> findById(
            @PathVariable UUID listId,
            @PathVariable UUID itemId,
            Authentication authentication
    ) {

        UUID userId = (UUID) authentication.getPrincipal();

        return ResponseEntity.ok(
                itemService.findById(
                        userId,
                        listId,
                        itemId
                )
        );
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<ItemResponse> update(
            @PathVariable UUID listId,
            @PathVariable UUID itemId,
            @Valid @RequestBody UpdateItemRequest request,
            Authentication authentication
    ) {

        UUID userId = (UUID) authentication.getPrincipal();

        return ResponseEntity.ok(
                itemService.update(
                        userId,
                        listId,
                        itemId,
                        request
                )
        );
    }

    @PatchMapping("/{itemId}/complete")
    public ResponseEntity<ItemResponse> toggleCompleted(
            @PathVariable UUID listId,
            @PathVariable UUID itemId,
            Authentication authentication
    ) {

        UUID userId = (UUID) authentication.getPrincipal();

        return ResponseEntity.ok(
                itemService.toggleCompleted(
                        userId,
                        listId,
                        itemId
                )
        );
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID listId,
            @PathVariable UUID itemId,
            Authentication authentication
    ) {

        UUID userId = (UUID) authentication.getPrincipal();

        itemService.delete(
                userId,
                listId,
                itemId
        );

        return ResponseEntity.noContent().build();
    }
}