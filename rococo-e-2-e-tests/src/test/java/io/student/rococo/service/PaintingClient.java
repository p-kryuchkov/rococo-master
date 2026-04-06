package io.student.rococo.service;

import io.qameta.allure.Step;
import io.student.rococo.model.PaintingJson;

import javax.annotation.Nonnull;

public interface PaintingClient {
    @Step("Create New Painting")
    @Nonnull
    PaintingJson createPainting(PaintingJson PaintingJson);

    @Step("Update Painting")
    @Nonnull
    PaintingJson updatePainting(PaintingJson PaintingJson);
}
