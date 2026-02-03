package io.student.rococo.controller;

import io.student.rococo.model.PaintingJson;
import io.student.rococo.service.PaintingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/painting")
public class PaintingController {
    private final PaintingService paintingService;

    @Autowired
    public PaintingController(PaintingService paintingService) {
        this.paintingService = paintingService;

    }

    @GetMapping
    public Page<PaintingJson> getAllPaintings(@PageableDefault Pageable pageable) {
        return paintingService.getAllPaintings(pageable);
    }

    @GetMapping("author/{id}")
    public Page<PaintingJson> getAllPaintingsByArtist(@PathVariable UUID id, @PageableDefault Pageable pageable) {
        return paintingService.getPaintingByArist(id, pageable);
    }

    @GetMapping("{id}")
    public PaintingJson getPaintingById(@PathVariable UUID id) {
        return paintingService.getPaintingById(id);
    }

    @PostMapping()
    public PaintingJson createPainting(@RequestBody PaintingJson paintingJson) {
        return paintingService.createPainting(paintingJson);
    }

    @PatchMapping()
    public PaintingJson updatePainting(@RequestBody PaintingJson paintingJson) {
        return paintingService.updatePainting(paintingJson);
    }
}
