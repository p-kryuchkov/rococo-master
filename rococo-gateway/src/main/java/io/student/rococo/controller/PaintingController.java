package io.student.rococo.controller;

import io.student.rococo.model.PaintingJson;
import io.student.rococo.service.grpc.GrpcMuseumClient;
import io.student.rococo.service.grpc.GrpcPaintingClient;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/painting")
public class PaintingController {
    private final GrpcPaintingClient paintingClient;


    @Autowired
    public PaintingController(GrpcPaintingClient paintingClient) {
        this.paintingClient = paintingClient;
    }

    @Nonnull
    @GetMapping
    public Page<PaintingJson> getAllPaintings(
            @Nullable @RequestParam(required = false) String title,
            @PageableDefault @Nonnull Pageable pageable) {
        if (title != null && !title.isBlank()) {
            return paintingClient.getPaintingsByTitle(title, pageable);
        }
        return paintingClient.getAllPaintings(pageable);
    }

    @Nonnull
    @GetMapping("author/{id}")
    public Page<PaintingJson> getAllPaintingsByArtist(@PathVariable @Nonnull UUID id, @PageableDefault @Nonnull Pageable pageable) {
        return paintingClient.getPaintingsByArtist(id, pageable);
    }

    @Nonnull
    @GetMapping("{id}")
    public PaintingJson getPaintingById(@PathVariable @Nonnull UUID id) {
        return paintingClient.getPaintingById(id);
    }

    @Nonnull
    @PostMapping()
    public PaintingJson createPainting(@RequestBody @Nonnull PaintingJson paintingJson) {
        return paintingClient.createPainting(paintingJson);
    }

    @Nonnull
    @PatchMapping()
    public PaintingJson updatePainting(@RequestBody @Nonnull PaintingJson paintingJson) {
        return paintingClient.updatePainting(paintingJson);
    }
}
