package io.student.rococo.service;

import io.student.rococo.data.entity.data.CountryEntity;
import io.student.rococo.model.CountryJson;

import java.util.Optional;

public interface CountryClient {
    CountryJson createCountry(CountryJson countryJson);

    Optional<CountryEntity> findByName(String name);
}
