package io.student.rococo.service;

import io.student.rococo.model.CountryJson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CountryService {
    public Page<CountryJson> getAllCountries(Pageable pageable);

    public CountryJson getCountryById(UUID id);
}
