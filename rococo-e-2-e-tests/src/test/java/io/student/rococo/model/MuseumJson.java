package io.student.rococo.model;

import io.student.rococo.grpc.MuseumResponse;

import java.util.UUID;

public record MuseumJson(UUID id,
                         String description,
                         String title,
                         String photo,
                         GeoJson geo) {
}
