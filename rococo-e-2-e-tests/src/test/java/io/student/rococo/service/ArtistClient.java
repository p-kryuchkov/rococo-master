package io.student.rococo.service;

import io.qameta.allure.Step;
import io.student.rococo.model.ArtistJson;
import io.student.rococo.model.UserJson;

import javax.annotation.Nonnull;

public interface ArtistClient {
    @Step("Create New Artist")
    @Nonnull
    ArtistJson createArtist(ArtistJson artistJson);

    @Step("Create Or Update Artist If Exist")
    @Nonnull
    ArtistJson createOrUpdateArtist(ArtistJson artistJson);

    @Step("Update Artist")
    @Nonnull
    ArtistJson updateArtist(ArtistJson artistJson);
}
