package io.student.rococo.data.repository;

import io.student.rococo.data.entity.PaintingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaintingRepository extends JpaRepository<PaintingEntity, UUID> {
    public Page<PaintingEntity> findByArtist_Id(UUID id, Pageable pageable);

    Page<PaintingEntity> findAllByTitleContainingIgnoreCase(String title, Pageable pageable);
}
