package io.student.rococo.model;

import io.student.rococo.data.entity.data.ArtistEntity;

import java.util.UUID;

import static io.student.rococo.utils.Base64Utils.encodeImageFromBytesToB64;

public record ArtistJson(UUID id, String name, String biography, String photo) {
    public static ArtistJson fromEntity(ArtistEntity artistEntity) {
        return new ArtistJson(
                artistEntity.getId(),
                artistEntity.getName(),
                artistEntity.getBiography(),
                artistEntity.getPhoto() == null
                        ? null
                        : encodeImageFromBytesToB64(artistEntity.getPhoto())
        );
    }
}
