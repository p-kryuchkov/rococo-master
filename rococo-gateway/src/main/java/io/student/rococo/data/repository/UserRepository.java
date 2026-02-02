package io.student.rococo.data.repository;

import io.student.rococo.data.entity.ArtistEntity;
import io.student.rococo.data.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
}
