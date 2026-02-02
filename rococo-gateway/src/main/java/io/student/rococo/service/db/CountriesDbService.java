package io.student.rococo.service.db;

import io.student.rococo.data.repository.CountryRepository;
import io.student.rococo.model.CountryJson;
import io.student.rococo.service.CountryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CountriesDbService implements CountryService {
    private final CountryRepository countryRepository;

    @Autowired
    public CountriesDbService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public Page<CountryJson> getAllCountries(Pageable pageable) {
        return countryRepository.findAll(pageable).map(countryEntity -> {
            return new CountryJson(countryEntity.getId(), countryEntity.getName());
        });
    }

    @Override
    public CountryJson getCountryById(UUID id) {
        return countryRepository.findById(id).map(countryEntity -> {
            return new CountryJson(countryEntity.getId(), countryEntity.getName());
        })
                .orElseThrow(() -> new EntityNotFoundException(
                "Country not found, id=" + id
        ));
    }
}
