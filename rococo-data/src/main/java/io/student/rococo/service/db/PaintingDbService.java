package io.student.rococo.service.db;

import io.student.rococo.data.entity.ArtistEntity;
import io.student.rococo.data.entity.MuseumEntity;
import io.student.rococo.data.entity.PaintingEntity;
import io.student.rococo.data.repository.ArtistRepository;
import io.student.rococo.data.repository.MuseumRepository;
import io.student.rococo.data.repository.PaintingRepository;
import io.student.rococo.exception.ArtistNotFoundException;
import io.student.rococo.exception.FieldValidationException;
import io.student.rococo.exception.MuseumNotFoundException;
import io.student.rococo.exception.PaintingNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static io.student.rococo.utils.DbUtils.parseUuid;

@Service
@Transactional(readOnly = true)
public class PaintingDbService {
    private final PaintingRepository paintingRepository;
    private final ArtistRepository artistRepository;
    private final MuseumRepository museumRepository;

    @Autowired
    public PaintingDbService(PaintingRepository paintingRepository,
                             ArtistRepository artistRepository,
                             MuseumRepository museumRepository) {
        this.paintingRepository = paintingRepository;
        this.artistRepository = artistRepository;
        this.museumRepository = museumRepository;
    }

    public PaintingEntity getById(String id) {
        return paintingRepository.findById(parseUuid(id))
                .orElseThrow(() -> new PaintingNotFoundException("Painting not found with id: " + id));
    }

    public Page<PaintingEntity> getByTitle(String title, Pageable pageable) {
        validateTitle(title);
        return paintingRepository.findAllByTitleContainingIgnoreCase(title.trim(), pageable);
    }

    public Page<PaintingEntity> getAll(Pageable pageable) {
        return paintingRepository.findAll(pageable);
    }

    public Page<PaintingEntity> getByArtistId(String artistId, Pageable pageable) {
        UUID artistUuid = parseUuid(artistId);
        return paintingRepository.findByArtist_Id(artistUuid, pageable);
    }

    @Transactional
    public PaintingEntity create(String title, String description, byte[] content, String artistId, String museumId) {
        validateTitle(title);
        ArtistEntity artist = artistRepository.findById(parseUuid(artistId))
                .orElseThrow(() -> new ArtistNotFoundException("Artist not found by id: " + artistId));
        MuseumEntity museum = null;
        if (museumId != null && !museumId.isBlank()) {
            museum = museumRepository.findById(parseUuid(museumId))
                    .orElseThrow(() -> new MuseumNotFoundException("Museum not found by id: " + museumId));
        }
        PaintingEntity entity = new PaintingEntity();
        entity.setTitle(title);
        entity.setDescription(description);
        entity.setArtist(artist);
        entity.setMuseum(museum);
        if (content != null && content.length > 0) {
            entity.setContent(content);
        }
        return paintingRepository.save(entity);
    }

    @Transactional
    public PaintingEntity update(String id, String title, String description, byte[] content, String artistId, String museumId) {
        PaintingEntity entity = paintingRepository.findById(parseUuid(id))
                .orElseThrow(() -> new PaintingNotFoundException("Painting not found with id: " + id));
        if (title != null) {
            validateTitle(title);
            entity.setTitle(title);
        }
        if (description != null) {
            entity.setDescription(description);
        }
        if (content != null) {
            if (content.length == 0) {
                entity.setContent(null); // очистить контент
            } else {
                entity.setContent(content);
            }
        }
        if (artistId != null && !artistId.isBlank()) {
            ArtistEntity artist = artistRepository.findById(parseUuid(artistId))
                    .orElseThrow(() -> new ArtistNotFoundException("Artist not found by id: " + artistId));
            entity.setArtist(artist);
        }
        if (museumId != null) {
            if (museumId.isBlank()) {
                entity.setMuseum(null);
            } else {
                MuseumEntity museum = museumRepository.findById(parseUuid(museumId))
                        .orElseThrow(() -> new MuseumNotFoundException("Museum not found by id: " + museumId));
                entity.setMuseum(museum);
            }
        }
        return paintingRepository.save(entity);
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new FieldValidationException("Title must not be blank");
        }
    }
}