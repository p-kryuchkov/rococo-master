package io.student.rococo.service;

import io.student.rococo.model.PaintingJson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PaintingService {
    Page<PaintingJson> getAllPaintings(Pageable pageable);

    Page<PaintingJson> getPaintingByArist(UUID idArtist, Pageable pageable);

    PaintingJson getPaintingById(UUID id);

    PaintingJson createPainting(PaintingJson paintingJson);

    PaintingJson updatePainting(PaintingJson paintingJson);
}
