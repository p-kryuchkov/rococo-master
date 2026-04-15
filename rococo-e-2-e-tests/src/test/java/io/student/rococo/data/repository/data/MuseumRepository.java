package io.student.rococo.data.repository.data;

import io.student.rococo.config.Config;
import io.student.rococo.data.entity.data.ArtistEntity;
import io.student.rococo.data.entity.data.MuseumEntity;
import jakarta.persistence.EntityManager;

import javax.annotation.Nonnull;

import java.util.Optional;
import java.util.UUID;

import static io.student.rococo.data.jpa.EntityManagers.em;

public class MuseumRepository {
    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.dataJdbcUrl());

    @Nonnull
    public MuseumEntity createMuseum(@Nonnull MuseumEntity museumEntity) {
        entityManager.joinTransaction();
        entityManager.persist(museumEntity);
        return museumEntity;
    }

    @Nonnull
    public MuseumEntity updateMuseum(@Nonnull MuseumEntity museumEntity) {
        entityManager.joinTransaction();
        return entityManager.merge(museumEntity);
    }

    @Nonnull
    public Optional<MuseumEntity> findById(@Nonnull UUID id) {
        return Optional.ofNullable(
                entityManager.find(MuseumEntity.class, id)
        );
    }

    @Nonnull
    public Optional<MuseumEntity> findByTitle(@Nonnull String title) {
        return entityManager.createQuery(
                        "select m from MuseumEntity m where m.title = :title",
                        MuseumEntity.class
                )
                .setParameter("title", title)
                .getResultStream()
                .findFirst();
    }
}