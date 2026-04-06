package io.student.rococo.model;

import io.student.rococo.data.entity.data.ArtistEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.UUID;

import static io.student.rococo.utils.Base64Utils.encodeImageFromBytesToB64;

public record ArtistJson(UUID id, String name, String biography, String photo) {
    public static @Nonnull ArtistJson fromEntity(@Nonnull ArtistEntity artistEntity) {
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
