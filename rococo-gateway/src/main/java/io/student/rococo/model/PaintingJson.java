package io.student.rococo.model;

import io.student.rococo.grpc.PaintingResponse;

import java.util.UUID;

import static io.student.rococo.utils.Base64Utils.encodeImageFromBytesToB64;

public record PaintingJson(UUID id,
                           String description,
                           String title,
                           String content,
                           ArtistJson artist,
                           MuseumJson museum
) {
    public static PaintingJson fromGrpcMessage(PaintingResponse paintingResponse) {

        UUID uuid = (paintingResponse.getId().isBlank())
                ? null
                : UUID.fromString(paintingResponse.getId());

        String contentB64 = paintingResponse.getContent().isEmpty()
                ? null
                : encodeImageFromBytesToB64(paintingResponse.getContent().toByteArray());

        ArtistJson artistJson = paintingResponse.hasArtist()
                ? ArtistJson.fromGrpcMessage(paintingResponse.getArtist())
                : null;

        MuseumJson museumJson = paintingResponse.hasMuseum()
                ? MuseumJson.fromGrpcMessage(paintingResponse.getMuseum())
                : null;

        return new PaintingJson(
                uuid,
                paintingResponse.getDescription(),
                paintingResponse.getTitle(),
                contentB64,
                artistJson,
                museumJson
        );
    }
}