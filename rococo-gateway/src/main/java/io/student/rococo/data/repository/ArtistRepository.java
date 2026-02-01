package io.student.rococo.data.repository;

import io.student.rococo.data.entity.ArtistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ArtistRepository extends JpaRepository<ArtistEntity, UUID> {
    public Optional<ArtistEntity> getByName(String name);
}
