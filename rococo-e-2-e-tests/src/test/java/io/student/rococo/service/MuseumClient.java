package io.student.rococo.service;

import io.qameta.allure.Step;
import io.student.rococo.model.MuseumJson;

import javax.annotation.Nonnull;

public interface MuseumClient {
    @Step("Create New Museum")
    @Nonnull
    MuseumJson createMuseum(MuseumJson museumJson);

    @Step("Update Museum")
    @Nonnull
    MuseumJson updateMuseum(MuseumJson museumJson);
}
