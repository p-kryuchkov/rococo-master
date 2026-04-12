package io.student.rococo.controller;

import io.student.rococo.model.MuseumJson;
import io.student.rococo.service.grpc.GrpcArtistClient;
import io.student.rococo.service.grpc.GrpcMuseumClient;
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

    @GetMapping
    public Page<MuseumJson> getAllMuseums(
            @RequestParam(required = false) String title,
            @PageableDefault Pageable pageable) {
        if (title != null && !title.isBlank()) {
            return museumClient.getMuseumsByTitle(title, pageable);
        }
        return museumClient.getAllMuseums(pageable);
    }

    @GetMapping("{id}")
    public MuseumJson getMuseumById(@PathVariable UUID id) {
        return museumClient.getMuseumById(id);
    }

    @PostMapping()
    public MuseumJson createMuseum(@RequestBody MuseumJson museumJson) {
        return museumClient.createMuseum(museumJson);
    }

    @PatchMapping()
    public MuseumJson updateMuseum(@RequestBody MuseumJson museumJson) {
        return museumClient.updateMuseum(museumJson);
    }
}
