package com.douglas.listmanager.feature.user.repository;

import com.douglas.listmanager.feature.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);

    Optional<User> findByIdAndActiveTrue(UUID id);

    java.util.List<User> findAllByActiveTrue();
}