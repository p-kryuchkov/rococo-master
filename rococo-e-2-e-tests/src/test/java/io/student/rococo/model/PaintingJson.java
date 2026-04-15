package io.student.rococo.model;

import io.student.rococo.data.entity.data.PaintingEntity;

import javax.annotation.Nonnull;
import java.util.UUID;

import static io.student.rococo.utils.Base64Utils.encodeImageFromBytesToB64;


public record PaintingJson(UUID id,
                           String description,
                           String title,
                           String content,
                           ArtistJson artist,
                           MuseumJson museum
) {
    public static @Nonnull PaintingJson fromEntity(@Nonnull final PaintingEntity paintingEntity) {
        return new PaintingJson(
                paintingEntity.getId(),
                paintingEntity.getDescription(),
                paintingEntity.getTitle(),
                paintingEntity.getContent() == null
                        ? null
                        : encodeImageFromBytesToB64(paintingEntity.getContent()),
                paintingEntity.getArtist() == null
                        ? null
                        : ArtistJson.fromEntity(paintingEntity.getArtist()),
                paintingEntity.getMuseum() == null
                        ? null
                        : MuseumJson.fromEntity(paintingEntity.getMuseum())
        );
    }
}