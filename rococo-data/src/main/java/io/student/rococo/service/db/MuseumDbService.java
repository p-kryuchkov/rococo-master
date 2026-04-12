package io.student.rococo.service.db;

import io.student.rococo.data.entity.ArtistEntity;
import io.student.rococo.data.entity.CountryEntity;
import io.student.rococo.data.entity.MuseumEntity;
import io.student.rococo.data.repository.ArtistRepository;
import io.student.rococo.data.repository.CountryRepository;
import io.student.rococo.data.repository.MuseumRepository;
import io.student.rococo.exception.CountryNotFoundException;
import io.student.rococo.exception.FieldValidationException;
import io.student.rococo.exception.MuseumNotFoundException;
import jakarta.persistence.EntityExistsException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

import static io.student.rococo.utils.DbUtils.parseUuid;

@Service
@Transactional(readOnly = true)
public class MuseumDbService {
    private final MuseumRepository museumRepository;
    private final CountryRepository countryRepository;

    public MuseumDbService(MuseumRepository museumRepository, CountryRepository countryRepository) {
        this.museumRepository = museumRepository;
        this.countryRepository = countryRepository;
    }

    public MuseumEntity getById(String id) {
        UUID uuid = parseUuid(id);

        return museumRepository.findById(uuid)
                .orElseThrow(() ->
                        new MuseumNotFoundException("Museum not found with id: " + id)
                );
    }

    public Page<MuseumEntity> getByTitle(String title, Pageable pageable) {
        validateTitle(title);
        return museumRepository.findAllByTitleContainingIgnoreCase(title.trim(), pageable);
    }

    public Page<MuseumEntity> getAll(Pageable pageable) {
        return museumRepository.findAll(pageable);
    }

    @Transactional
    public MuseumEntity create(String title,
                               String description,
                               String city,
                               String countryId,
                               byte[] photo) {

        validateTitle(title);
        validateCountryId(countryId);

        museumRepository.getByTitle(title)
                .ifPresent(e -> {
                    throw new EntityExistsException("Museum already exists with title: " + title);
                });

        CountryEntity country = countryRepository.findById(parseUuid(countryId))
                .orElseThrow(() -> new CountryNotFoundException("Country not found with id: " + countryId));

        MuseumEntity entity = new MuseumEntity();
        entity.setTitle(title);
        entity.setDescription(description);
        entity.setCity(city);
        entity.setCountry(country);
        if (photo != null && photo.length > 0) {
            entity.setPhoto(photo);
        }
        return museumRepository.save(entity);
    }

    @Transactional
    public MuseumEntity update(String id, String title, String description, String city, String countryId, byte[] photo) {
        MuseumEntity entity = museumRepository.findById(parseUuid(id))
                .orElseThrow(() -> new MuseumNotFoundException("Museum not found with id: " + id));
        if (title != null) {
            validateTitle(title);
            museumRepository.getByTitle(title)
                    .filter(existing -> !existing.getId().equals(entity.getId()))
                    .ifPresent(e -> {
                        throw new EntityExistsException("Museum already exists with title: " + title);
                    });
            entity.setTitle(title);
        }
        if (description != null) {
            entity.setDescription(description);
        }
        if (city != null) {
            entity.setCity(city);
        }
        if (countryId != null) {
            CountryEntity country = countryRepository.findById(parseUuid(countryId))
                    .orElseThrow(() -> new CountryNotFoundException("Country not found with id: " + countryId));
            entity.setCountry(country);
        }
        if (photo != null) {
            if (photo.length == 0) {
                entity.setPhoto(null);
            } else {
                entity.setPhoto(photo);
            }
        }
        return museumRepository.save(entity);
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new FieldValidationException("Title must not be blank");
        }
    }

    private void validateCountryId(String countryId) {
        if (countryId == null || countryId.isBlank()) {
            throw new FieldValidationException("Country Id must not be blank");
        }
    }
}