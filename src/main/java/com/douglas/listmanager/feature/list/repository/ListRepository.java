package com.douglas.listmanager.feature.list.repository;

import com.douglas.listmanager.feature.list.entity.UserList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ListRepository extends JpaRepository<UserList, UUID> {

    List<UserList> findAllByUserIdAndActiveTrue(UUID userId);

    Optional<UserList> findByIdAndUserIdAndActiveTrue(
            UUID id,
            UUID userId
    );
}