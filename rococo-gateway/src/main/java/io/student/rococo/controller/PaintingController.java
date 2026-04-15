package io.student.rococo.controller;

import io.student.rococo.model.PaintingJson;
import io.student.rococo.service.grpc.GrpcMuseumClient;
import io.student.rococo.service.grpc.GrpcPaintingClient;
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

    @GetMapping
    public Page<PaintingJson> getAllPaintings(
            @RequestParam(required = false) String title,
            @PageableDefault Pageable pageable) {
        if (title != null && !title.isBlank()) {
            return paintingClient.getPaintingsByTitle(title, pageable);
        }
        return paintingClient.getAllPaintings(pageable);
    }

    @GetMapping("author/{id}")
    public Page<PaintingJson> getAllPaintingsByArtist(@PathVariable UUID id, @PageableDefault Pageable pageable) {
        return paintingClient.getPaintingsByArtist(id, pageable);
    }

    @GetMapping("{id}")
    public PaintingJson getPaintingById(@PathVariable UUID id) {
        return paintingClient.getPaintingById(id);
    }

    @PostMapping()
    public PaintingJson createPainting(@RequestBody PaintingJson paintingJson) {
        return paintingClient.createPainting(paintingJson);
    }

    @PatchMapping()
    public PaintingJson updatePainting(@RequestBody PaintingJson paintingJson) {
        return paintingClient.updatePainting(paintingJson);
    }
}
