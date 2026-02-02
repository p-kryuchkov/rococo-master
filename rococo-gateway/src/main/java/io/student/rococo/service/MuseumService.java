package io.student.rococo.service;

import io.student.rococo.model.MuseumJson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MuseumService {
    public Page<MuseumJson> getAllMuseums(Pageable pageable);

    public MuseumJson getMuseumById(UUID id);

    public MuseumJson createMuseum(MuseumJson museumJson);

    public MuseumJson updateMuseum(MuseumJson museumJson);
}
