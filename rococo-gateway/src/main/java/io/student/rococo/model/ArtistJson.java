package io.student.rococo.model;

import io.student.rococo.data.entity.ArtistEntity;
import io.student.rococo.grpc.ArtistResponse;

import java.util.UUID;

import static io.student.rococo.utils.Base64Utils.encodeImageFromBytesToB64;

public record ArtistJson(UUID id, String name, String biography, String photo) {
    public static ArtistJson fromEntity(ArtistEntity entity) {


        return new ArtistJson(
                entity.getId(),
                entity.getName(),
                entity.getBiography(),
                entity.getPhoto() == null
                        ? null
                        : encodeImageFromBytesToB64(entity.getPhoto())
        );
    }

    public static ArtistJson fromGrpcMessage(ArtistResponse response) {


        return new ArtistJson(
                UUID.fromString(response.getId()),
                response.getName(),
                response.getBiography(),
                response.getPhoto().isEmpty()
                ? null
                : encodeImageFromBytesToB64(response.getPhoto().toByteArray())
        );
    }
}
