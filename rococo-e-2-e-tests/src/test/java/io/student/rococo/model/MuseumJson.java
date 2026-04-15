package io.student.rococo.model;

import io.student.rococo.data.entity.data.MuseumEntity;

import javax.annotation.Nonnull;
import java.util.UUID;

import static io.student.rococo.utils.Base64Utils.encodeImageFromBytesToB64;

public record MuseumJson(UUID id,
                         String description,
                         String title,
                         String photo,
                         GeoJson geo) {
    public static @Nonnull MuseumJson fromEntity(@Nonnull final MuseumEntity museumEntity) {
        CountryJson country = CountryJson.fromEntity(museumEntity.getCountry());
        GeoJson geo = new GeoJson(museumEntity.getCity(), country);
        return new MuseumJson(
                museumEntity.getId(),
                museumEntity.getDescription(),
                museumEntity.getTitle(),
                museumEntity.getPhoto() == null
                        ? null
                        : encodeImageFromBytesToB64(museumEntity.getPhoto()),
                geo
        );
    }
}
