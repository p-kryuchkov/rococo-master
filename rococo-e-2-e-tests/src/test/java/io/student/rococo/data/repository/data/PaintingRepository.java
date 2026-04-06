package io.student.rococo.data.repository.data;

import io.student.rococo.config.Config;
import io.student.rococo.data.entity.data.PaintingEntity;
import jakarta.persistence.EntityManager;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

import static io.student.rococo.data.jpa.EntityManagers.em;

public class PaintingRepository {
    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.dataJdbcUrl());

    @Nonnull
    public PaintingEntity createPainting(@Nonnull PaintingEntity paintingEntity) {
        entityManager.joinTransaction();
        entityManager.persist(paintingEntity);
        return paintingEntity;
    }

    @Nonnull
    public PaintingEntity updatePainting(@Nonnull PaintingEntity paintingEntity) {
        entityManager.joinTransaction();
        return entityManager.merge(paintingEntity);
    }

    @Nonnull
    public Optional<PaintingEntity> findById(@Nonnull UUID id) {
        return Optional.ofNullable(
                entityManager.find(PaintingEntity.class, id)
        );
    }
}
