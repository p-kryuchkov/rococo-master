package io.student.rococo.service.db;

import io.student.rococo.data.entity.CountryEntity;
import io.student.rococo.data.entity.MuseumEntity;
import io.student.rococo.data.repository.MuseumRepository;
import io.student.rococo.model.MuseumJson;
import io.student.rococo.service.MuseumService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.UUID;

@Component
public class MuseumDbService implements MuseumService {
    private final MuseumRepository museumRepository;

    @Autowired
    public MuseumDbService(MuseumRepository museumRepository) {
        this.museumRepository = museumRepository;
    }


    @Override
    public Page<MuseumJson> getAllMuseums(Pageable pageable) {
        return museumRepository.findAll(pageable).map(MuseumJson::fromEntity);
    }

    @Override
    public MuseumJson getMuseumById(UUID id) {
        return museumRepository.findById(id).map(MuseumJson::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Museum not found, id=" + id
                ));
    }

    @Override
    public MuseumJson createMuseum(MuseumJson museumJson) {
        MuseumEntity museumEntity = new MuseumEntity();
        CountryEntity countryEntity = new CountryEntity();
        if (museumRepository.getByTitle(museumJson.title()).isPresent()) {
            throw new IllegalArgumentException("Museum with this name exists");
        }
        museumEntity.setTitle(museumJson.title());
        museumEntity.setCity(museumJson.city());
        museumEntity.setDescription(museumJson.description());
        if (museumJson.photo() != null) {
            museumEntity.setPhoto(Base64.getDecoder().decode(museumJson.photo()));
        }
        countryEntity.setId(museumJson.country().id());
        countryEntity.setName(museumJson.country().name());
        museumEntity.setCountry(countryEntity);

        return MuseumJson.fromEntity(museumRepository.save(museumEntity));
    }

    @Override
    public MuseumJson updateMuseum(MuseumJson museumJson) {
        MuseumEntity museumEntity = museumRepository.findById(museumJson.id())
                .orElseThrow(() -> new EntityNotFoundException("Museum not found"));
        CountryEntity countryEntity = new CountryEntity();

        if (museumJson.title() != null) museumEntity.setTitle(museumJson.title());
        if (museumJson.city() != null) museumEntity.setCity(museumJson.city());
        if (museumJson.description() != null) museumEntity.setDescription(museumJson.description());
        if (museumJson.photo() != null) {
            museumEntity.setPhoto(Base64.getDecoder().decode(museumJson.photo()));
        }
        if (museumJson.country() != null) {
            countryEntity.setId(museumJson.country().id());
            countryEntity.setName(museumJson.country().name());
            museumEntity.setCountry(countryEntity);
        }
        return MuseumJson.fromEntity(museumRepository.save(museumEntity));
    }
}
