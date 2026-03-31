package io.student.rococo.service.db;

import io.student.rococo.data.entity.ArtistEntity;
import io.student.rococo.data.repository.ArtistRepository;
import io.student.rococo.exception.ArtistNotFoundException;
import io.student.rococo.exception.FieldValidationException;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static io.student.rococo.utils.DbUtils.parseUuid;
import java.util.UUID;

@Service
@Transactional(readOnly = true)

public class ArtistDbService {
    private final ArtistRepository artistRepository;

    @Autowired
    public ArtistDbService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public ArtistEntity getById(String id) {
        UUID uuid = parseUuid(id);
        return artistRepository.findById(uuid)
                .orElseThrow(() ->
                        new ArtistNotFoundException("Artist not found with id: " + id)
                );
    }

    public Page<ArtistEntity> getAll(Pageable pageable) {
        return artistRepository.findAll(pageable);
    }

    @Transactional
    public ArtistEntity create(String name, String biography, byte[] photo) {
        validateName(name);
        validateBiography(biography);
        artistRepository.getByName(name)
                .ifPresent(e -> {
                    throw new EntityExistsException(
                            "Artist already exists with name: " + name
                    );
                });

        ArtistEntity entity = new ArtistEntity();
        entity.setName(name);
        entity.setBiography(biography);

        if (photo != null && photo.length > 0) {
            entity.setPhoto(photo);
        }

        return artistRepository.save(entity);
    }

    @Transactional
    public ArtistEntity update(String id, String name, String biography, byte[] photo) {
        UUID uuid = parseUuid(id);

        ArtistEntity entity = artistRepository.findById(uuid)
                .orElseThrow(() ->
                        new ArtistNotFoundException("Artist not found with id: " + id)
                );

        if (name != null) {
            validateName(name);
            artistRepository.getByName(name)
                    .filter(existing -> !existing.getId().equals(entity.getId()))
                    .ifPresent(e -> {
                        throw new EntityExistsException(
                                "Artist already exists with name: " + name
                        );
                    });
            entity.setName(name);
        }

        if (biography != null) {
            validateBiography(biography);
            entity.setBiography(biography);
        }

        if (photo != null) {
            if (photo.length == 0) {
                entity.setPhoto(null);
            } else {
                entity.setPhoto(photo);
            }
        }
        return artistRepository.save(entity);
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new FieldValidationException("Name must not be blank");
        }
    }

    private void validateBiography(String biography) {
        if (biography == null || biography.isBlank()) {
            throw new FieldValidationException("Biography must not be blank");
        }
    }
}