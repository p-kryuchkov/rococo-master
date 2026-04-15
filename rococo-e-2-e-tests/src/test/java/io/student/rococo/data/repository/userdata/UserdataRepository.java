package io.student.rococo.data.repository.userdata;

import io.student.rococo.config.Config;
import io.student.rococo.data.entity.userdata.UserDataEntity;
import jakarta.persistence.EntityManager;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

import static io.student.rococo.data.jpa.EntityManagers.em;

public class UserdataRepository {
    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.userdataJdbcUrl());

    @Nonnull
    public UserDataEntity createUserData(@Nonnull UserDataEntity userDataEntity) {
        entityManager.joinTransaction();
        entityManager.persist(userDataEntity);
        return userDataEntity;
    }

    @Nonnull
    public Optional<UserDataEntity> findByUsername(@Nonnull String username) {
        return Optional.ofNullable(
                entityManager.createQuery("select ud from UserDataEntity where ud.username = :username",UserDataEntity.class)
                        .setParameter("username", username)
                        .getResultList()
                        .getFirst()
        );
    }
}
