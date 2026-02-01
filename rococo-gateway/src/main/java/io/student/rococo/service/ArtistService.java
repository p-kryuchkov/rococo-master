package io.student.rococo.service;

import io.student.rococo.model.ArtistJson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ArtistService {
    public Page<ArtistJson> getAllArtists(Pageable pageable);

    public ArtistJson getArtistById(UUID id);

    public ArtistJson createArtist(ArtistJson artist);

    public ArtistJson updateArtist(ArtistJson artist);
}
