package io.student.rococo.model;

import io.student.rococo.data.entity.ArtistEntity;
import io.student.rococo.data.entity.MuseumEntity;
import io.student.rococo.data.entity.PaintingEntity;

import java.util.UUID;

import static io.student.rococo.utils.Base64Utils.encodeImageFromBytesToB64;

public record PaintingJson(UUID id,
                           String description,
                           String title,
                           String content,
                           ArtistJson artist,
                           MuseumJson museum
) {
    public static PaintingJson fromEntity(PaintingEntity paintingEntity) {
        final ArtistEntity artistEntity = paintingEntity.getArtist();
        final MuseumEntity museumEntity = paintingEntity.getMuseum();
        return new PaintingJson(paintingEntity.getId(),
                paintingEntity.getDescription(),
                paintingEntity.getTitle(),
                paintingEntity.getContent() == null
                        ? null
                        : encodeImageFromBytesToB64(paintingEntity.getContent()),
                ArtistJson.fromEntity(artistEntity),
                MuseumJson.fromEntity(museumEntity));
    }

}
