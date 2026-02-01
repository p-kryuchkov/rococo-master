package io.student.rococo.controller;

import io.student.rococo.model.ArtistJson;
import io.student.rococo.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/artist")
public class ArtistController {
    private final ArtistService artistService;

    @Autowired
    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;

    }

    @GetMapping
    public Page<ArtistJson> getAllArtists(@PageableDefault Pageable pageable) {
        return artistService.getAllArtists(pageable);
    }

    @GetMapping("{id}")
    public ArtistJson getArtistById(@PathVariable UUID id) {
        return artistService.getArtistById(id);
    }

    @PostMapping()
    public ArtistJson createArtist(@RequestBody ArtistJson artistJson) {
        return artistService.createArtist(artistJson);
    }

    @PatchMapping()
    public ArtistJson updateArtist(@RequestBody ArtistJson artistJson) {
        return artistService.updateArtist(artistJson);
    }
}
