package io.student.rococo.data.repository.data;

import io.student.rococo.config.Config;
import io.student.rococo.data.entity.data.ArtistEntity;
import jakarta.persistence.EntityManager;

import javax.annotation.Nonnull;

import java.util.Optional;
import java.util.UUID;

import static io.student.rococo.data.jpa.EntityManagers.em;

public class ArtistRepository {
    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.dataJdbcUrl());

    @Nonnull
    public ArtistEntity createArtist(@Nonnull ArtistEntity artistEntity) {
        entityManager.joinTransaction();
        entityManager.persist(artistEntity);
        return artistEntity;
    }

    @Nonnull
    public ArtistEntity updateUser(@Nonnull ArtistEntity artistEntity) {
        entityManager.joinTransaction();
        return entityManager.merge(artistEntity);
    }

    @Nonnull
    public Optional<ArtistEntity> findById(@Nonnull UUID id) {
        return Optional.ofNullable(
                entityManager.find(ArtistEntity.class, id)
        );
    }
}
