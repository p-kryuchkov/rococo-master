package io.student.rococo.service.db;

import io.student.rococo.data.entity.ArtistEntity;
import io.student.rococo.data.repository.ArtistRepository;
import io.student.rococo.model.ArtistJson;
import io.student.rococo.service.ArtistService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static io.student.rococo.utils.Base64Utils.decodeImageFromB64ToBytes;

@Component
public class ArtistDbService implements ArtistService {
    private final ArtistRepository artistRepository;

    @Autowired
    public ArtistDbService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }


    @Override
    public Page<ArtistJson> getAllArtists(Pageable pageable) {
        return artistRepository.findAll(pageable).map(entity -> {
            return ArtistJson.fromEntity(entity);
        });
    }

    @Override
    public ArtistJson getArtistById(UUID id) {
        return ArtistJson.fromEntity(
                artistRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Artist not found, id=" + id
                        ))
        );
    }

    @Override
    public ArtistJson createArtist(ArtistJson artistJson) {
        ArtistEntity artistEntity = new ArtistEntity();
        if (artistRepository.getByName(artistJson.name()).isPresent())
            throw new IllegalArgumentException("Artist with this name exists");
        artistEntity.setName(artistJson.name());
        artistEntity.setBiography(artistJson.biography());
        if (artistJson.photo() != null) {
            artistEntity.setPhoto(decodeImageFromB64ToBytes(artistJson.photo()));
        }
        return ArtistJson.fromEntity(
                artistRepository.save(artistEntity));
    }

    @Override
    public ArtistJson updateArtist(ArtistJson artistJson) {

        ArtistEntity resultEntity = artistRepository.findById(artistJson.id())
                .orElseThrow(() -> new EntityNotFoundException("Artist not found"));

        if (artistJson.name() != null) resultEntity.setName(artistJson.name());
        if (artistJson.biography() != null) resultEntity.setBiography(artistJson.biography());
        if (artistJson.photo() != null) resultEntity.setPhoto(decodeImageFromB64ToBytes(artistJson.photo()));

        return ArtistJson.fromEntity(
                artistRepository.save(resultEntity));
    }
}
