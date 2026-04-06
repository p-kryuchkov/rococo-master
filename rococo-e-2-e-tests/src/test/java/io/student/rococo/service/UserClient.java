package io.student.rococo.service;

import io.qameta.allure.Step;
import io.student.rococo.model.PaintingJson;
import io.student.rococo.model.UserJson;

import javax.annotation.Nonnull;

public interface UserClient {
    @Step("Create New User")
    @Nonnull
    UserJson createUser(@Nonnull String username, @Nonnull String password);
}
