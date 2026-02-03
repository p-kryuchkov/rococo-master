package io.student.rococo.service.db;

import io.student.rococo.data.entity.ArtistEntity;
import io.student.rococo.data.entity.MuseumEntity;
import io.student.rococo.data.entity.PaintingEntity;
import io.student.rococo.data.repository.PaintingRepository;
import io.student.rococo.model.PaintingJson;
import io.student.rococo.service.PaintingService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static io.student.rococo.utils.Base64Utils.decodeImageFromB64ToBytes;

@Component
public class PaintingDbService implements PaintingService {
    private final PaintingRepository paintingRepository;

    @Autowired
    public PaintingDbService(PaintingRepository paintingRepository) {
        this.paintingRepository = paintingRepository;
    }

    @Override
    public Page<PaintingJson> getAllPaintings(Pageable pageable) {
        return paintingRepository.findAll(pageable).map(PaintingJson::fromEntity);
    }

    @Override
    public Page<PaintingJson> getPaintingByArist(UUID idArtist, Pageable pageable) {
        return paintingRepository.findByArtist_Id(idArtist, pageable).map(PaintingJson::fromEntity);
    }

    @Override
    public PaintingJson getPaintingById(UUID id) {
        return paintingRepository.findById(id).map(PaintingJson::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Painting not found, id=" + id
                ));
    }

    @Override
    public PaintingJson createPainting(PaintingJson paintingJson) {
        PaintingEntity paintingEntity = new PaintingEntity();
        ArtistEntity artistEntity = new ArtistEntity();
        MuseumEntity museumEntity = new MuseumEntity();

        paintingEntity.setTitle(paintingJson.title());
        paintingEntity.setDescription(paintingJson.description());
        if (paintingJson.content() != null) {
            paintingEntity.setContent(decodeImageFromB64ToBytes(paintingJson.content()));
        }
        artistEntity.setId(paintingJson.artist().id());
        museumEntity.setId(paintingJson.museum().id());
        paintingEntity.setArtist(artistEntity);
        paintingEntity.setMuseum(museumEntity);

        return PaintingJson.fromEntity(paintingRepository.save(paintingEntity));
    }

    @Override
    public PaintingJson updatePainting(PaintingJson paintingJson) {
        PaintingEntity paintingEntity = paintingRepository.findById(paintingJson.id())
                .orElseThrow(() -> new EntityNotFoundException("Painting not found"));
        ArtistEntity artistEntity = paintingEntity.getArtist();
        MuseumEntity museumEntity = paintingEntity.getMuseum();

        if (paintingJson.title() != null) paintingEntity.setTitle(paintingJson.title());
        if (paintingJson.description() != null) paintingEntity.setDescription(paintingJson.description());
        if (paintingJson.content() != null) {
            paintingEntity.setContent(decodeImageFromB64ToBytes(paintingJson.content()));
        }
        if (paintingJson.artist().id() != null) {
            artistEntity.setId(paintingJson.artist().id());
            paintingEntity.setArtist(artistEntity);
        }
        if (paintingJson.museum().id() != null) {
            museumEntity.setId(paintingJson.museum().id());
            paintingEntity.setMuseum(museumEntity);
        }

        return PaintingJson.fromEntity(paintingRepository.save(paintingEntity));
    }
}
