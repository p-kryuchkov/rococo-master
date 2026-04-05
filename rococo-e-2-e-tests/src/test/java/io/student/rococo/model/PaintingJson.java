package io.student.rococo.model;

import java.util.UUID;


public record PaintingJson(UUID id,
                           String description,
                           String title,
                           String content,
                           ArtistJson artist,
                           MuseumJson museum
) {
}