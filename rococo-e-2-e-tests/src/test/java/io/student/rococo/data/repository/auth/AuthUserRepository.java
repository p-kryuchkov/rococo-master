package io.student.rococo.data.repository.auth;

import io.student.rococo.config.Config;
import io.student.rococo.data.entity.auth.AuthUserEntity;
import jakarta.persistence.EntityManager;

import javax.annotation.Nonnull;

import static io.student.rococo.data.jpa.EntityManagers.em;

public class AuthUserRepository {
    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.authJdbcUrl());

    public @Nonnull AuthUserEntity createUser(@Nonnull AuthUserEntity user) {
        entityManager.joinTransaction();
        entityManager.persist(user);
        return user;
    }
}
