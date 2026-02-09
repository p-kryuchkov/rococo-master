package io.student.rococo.model;

import io.student.rococo.data.entity.CountryEntity;
import io.student.rococo.data.entity.MuseumEntity;

import java.util.UUID;

import static io.student.rococo.utils.Base64Utils.encodeImageFromBytesToB64;

public record MuseumJson(UUID id,
                         String description,
                         String title,
                         String photo,
                         GeoJson geo) {
    public static MuseumJson fromEntity(MuseumEntity museumEntity) {
        final CountryEntity country = museumEntity.getCountry();
        return new MuseumJson(
                museumEntity.getId(),
                museumEntity.getDescription(),
                museumEntity.getTitle(),
                museumEntity.getPhoto() == null
                        ? null
                        : encodeImageFromBytesToB64(museumEntity.getPhoto()),
                new GeoJson(
                        museumEntity.getCity(),
                        country == null ? null
                                : new CountryJson(
                                country.getId(),
                                country.getName()))
        );
    }
}
