package io.student.rococo.model;

import io.student.rococo.data.entity.CountryEntity;
import io.student.rococo.data.entity.MuseumEntity;

import java.util.Base64;
import java.util.UUID;

public record MuseumJson(UUID id,
                         String description,
                         String title,
                         String city,
                         String photo,
                         CountryJson country) {
    public static MuseumJson fromEntity(MuseumEntity museumEntity) {
        final CountryEntity country = museumEntity.getCountry();
        return new MuseumJson(
                museumEntity.getId(),
                museumEntity.getDescription(),
                museumEntity.getTitle(),
                museumEntity.getCity(),
                museumEntity.getPhoto() == null
                        ? null
                        : Base64.getEncoder().encodeToString(museumEntity.getPhoto()),
                new CountryJson(country.getId(),
                        country.getName())
        );
    }
}
