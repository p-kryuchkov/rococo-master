package io.student.rococo.data.repository.data;

import io.student.rococo.config.Config;
import io.student.rococo.data.entity.data.CountryEntity;
import jakarta.persistence.EntityManager;

import javax.annotation.Nonnull;

import static io.student.rococo.data.jpa.EntityManagers.em;

public class CountryRepository {
    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.dataJdbcUrl());

    @Nonnull
    public CountryEntity createCountry(@Nonnull CountryEntity countryEntity) {
        entityManager.joinTransaction();
        entityManager.persist(countryEntity);
        return countryEntity;
    }
}
