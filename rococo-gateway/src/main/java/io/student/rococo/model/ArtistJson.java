package io.student.rococo.model;

import io.student.rococo.data.entity.ArtistEntity;

import java.util.Base64;
import java.util.UUID;

public record ArtistJson(UUID id, String name, String biography, String photo) {
        public static ArtistJson fromEntity(ArtistEntity entity) {

        String photoBase64 = entity.getPhoto() == null
                ? null
                : Base64.getEncoder().encodeToString(entity.getPhoto());

        return new ArtistJson(
                entity.getId(),
                entity.getName(),
                entity.getBiography(),
                photoBase64
        );
    }
}
