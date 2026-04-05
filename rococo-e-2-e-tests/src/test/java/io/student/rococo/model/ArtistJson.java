package io.student.rococo.model;

import io.student.rococo.grpc.ArtistResponse;

import java.util.UUID;

public record ArtistJson(UUID id, String name, String biography, String photo) {
}
