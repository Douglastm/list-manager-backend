package com.douglas.listmanager.feature.list.controller;

import com.douglas.listmanager.feature.list.dto.CreateListRequest;
import com.douglas.listmanager.feature.list.dto.ListResponse;
import com.douglas.listmanager.feature.list.dto.UpdateListRequest;
import com.douglas.listmanager.feature.list.service.ListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lists")
@RequiredArgsConstructor
public class ListController {

    private final ListService listService;

    @PostMapping
    public ResponseEntity<ListResponse> create(
            @Valid @RequestBody CreateListRequest request,
            Authentication authentication
    ) {

        UUID userId = (UUID) authentication.getPrincipal();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(listService.create(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<ListResponse>> findAll(
            Authentication authentication
    ) {

        UUID userId = (UUID) authentication.getPrincipal();

        return ResponseEntity.ok(
                listService.findAll(userId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListResponse> findById(
            @PathVariable UUID id,
            Authentication authentication
    ) {

        UUID userId = (UUID) authentication.getPrincipal();

        return ResponseEntity.ok(
                listService.findById(userId, id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ListResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateListRequest request,
            Authentication authentication
    ) {

        UUID userId = (UUID) authentication.getPrincipal();

        return ResponseEntity.ok(
                listService.update(userId, id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            Authentication authentication
    ) {

        UUID userId = (UUID) authentication.getPrincipal();

        listService.delete(userId, id);

        return ResponseEntity.noContent().build();
    }
}