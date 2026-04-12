package io.student.rococo.controller;

import io.student.rococo.model.ArtistJson;
import io.student.rococo.service.grpc.GrpcArtistClient;
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

    @GetMapping
    public Page<ArtistJson> getAllArtists(
            @RequestParam(required = false) String name,
            @PageableDefault Pageable pageable) {
        if (name != null && !name.isBlank()) {
            return grpcArtistClient.getArtistsByName(name, pageable);
        }
        return grpcArtistClient.getAllArtists(pageable);
    }

    @GetMapping("{id}")
    public ArtistJson getArtistById(@PathVariable UUID id) {
        return grpcArtistClient.getArtistById(id);
    }

    @PostMapping()
    public ArtistJson createArtist(@RequestBody ArtistJson artistJson) {
        return grpcArtistClient.createArtist(artistJson);
    }

    @PatchMapping()
    public ArtistJson updateArtist(@RequestBody ArtistJson artistJson) {
        return grpcArtistClient.updateArtist(artistJson);
    }
}
