package io.student.rococo.data.repository;

import io.student.rococo.data.entity.UserDataEntity;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserDataEntity, UUID> {
    @Nonnull
    Optional<UserDataEntity> findByUsername(@Nonnull String username);
}
