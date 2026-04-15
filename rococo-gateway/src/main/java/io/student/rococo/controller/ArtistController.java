package io.student.rococo.controller;

import io.student.rococo.model.ArtistJson;
import io.student.rococo.service.grpc.GrpcArtistClient;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/artist")
public class ArtistController {
    private final GrpcArtistClient grpcArtistClient;

    @Autowired
    public ArtistController(GrpcArtistClient grpcArtistClient) {
        this.grpcArtistClient = grpcArtistClient;

    }

    @Nonnull
    @GetMapping
    public Page<ArtistJson> getAllArtists(
            @Nullable @RequestParam(required = false) String name,
            @PageableDefault @Nonnull Pageable pageable) {
        if (name != null && !name.isBlank()) {
            return grpcArtistClient.getArtistsByName(name, pageable);
        }
        return grpcArtistClient.getAllArtists(pageable);
    }

    @Nonnull
    @GetMapping("{id}")
    public ArtistJson getArtistById(@PathVariable @Nonnull UUID id) {
        return grpcArtistClient.getArtistById(id);
    }

    @Nonnull
    @PostMapping()
    public ArtistJson createArtist(@RequestBody @Nonnull ArtistJson artistJson) {
        return grpcArtistClient.createArtist(artistJson);
    }

    @Nonnull
    @PatchMapping()
    public ArtistJson updateArtist(@RequestBody @Nonnull ArtistJson artistJson) {
        return grpcArtistClient.updateArtist(artistJson);
    }
}
