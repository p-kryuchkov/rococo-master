package io.student.rococo.data.repository;

import io.student.rococo.data.entity.PaintingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaintingRepository extends JpaRepository<PaintingEntity, UUID> {
}
