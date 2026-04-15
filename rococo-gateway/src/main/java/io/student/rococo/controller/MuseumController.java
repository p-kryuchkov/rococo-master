package io.student.rococo.controller;

import io.student.rococo.model.MuseumJson;
import io.student.rococo.service.grpc.GrpcArtistClient;
import io.student.rococo.service.grpc.GrpcMuseumClient;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/museum")
public class MuseumController {
    private final GrpcMuseumClient museumClient;

    public MuseumController(GrpcMuseumClient museumClient) {
        this.museumClient = museumClient;
    }

    @Nonnull
    @GetMapping
    public Page<MuseumJson> getAllMuseums(
            @Nullable @RequestParam(required = false) String title,
            @PageableDefault @Nonnull Pageable pageable) {
        if (title != null && !title.isBlank()) {
            return museumClient.getMuseumsByTitle(title, pageable);
        }
        return museumClient.getAllMuseums(pageable);
    }

    @Nonnull
    @GetMapping("{id}")
    public MuseumJson getMuseumById(@PathVariable @Nonnull UUID id) {
        return museumClient.getMuseumById(id);
    }

    @Nonnull
    @PostMapping()
    public MuseumJson createMuseum(@RequestBody @Nonnull MuseumJson museumJson) {
        return museumClient.createMuseum(museumJson);
    }

    @Nonnull
    @PatchMapping()
    public MuseumJson updateMuseum(@RequestBody @Nonnull MuseumJson museumJson) {
        return museumClient.updateMuseum(museumJson);
    }
}
