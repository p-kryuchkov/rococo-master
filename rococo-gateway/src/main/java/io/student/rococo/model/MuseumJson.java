package io.student.rococo.model;

import io.student.rococo.grpc.MuseumResponse;

import java.util.UUID;

import static io.student.rococo.utils.Base64Utils.encodeImageFromBytesToB64;

public record MuseumJson(UUID id,
                         String description,
                         String title,
                         String photo,
                         GeoJson geo) {
    public static MuseumJson fromGrpcMessage(MuseumResponse museumResponse) {
        String photo = museumResponse.getPhoto().isEmpty()
                ? null
                : encodeImageFromBytesToB64(museumResponse.getPhoto().toByteArray());
        UUID uuid = (museumResponse.getId().isBlank())
                ? null
                : UUID.fromString(museumResponse.getId());
        GeoJson geoJson = null;
        if (museumResponse.hasGeo()) {
            geoJson = new GeoJson(
                    museumResponse.getGeo().getCity(),
                    museumResponse.getGeo().getCountryId().isBlank()
                            ? null
                            : new CountryJson(
                            UUID.fromString(museumResponse.getGeo().getCountryId()),
                            museumResponse.getGeo().getCountryName()
                    )
            );
        }

        return new MuseumJson(
                uuid,
                museumResponse.getDescription(),
                museumResponse.getTitle(),
                photo,
                geoJson
        );
    }
}
