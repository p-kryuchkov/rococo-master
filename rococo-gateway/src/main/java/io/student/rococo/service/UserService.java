package io.student.rococo.service;

import io.student.rococo.model.ArtistJson;
import io.student.rococo.model.UserJson;

import java.util.UUID;

public interface UserService {
    public UserJson getUserByUsername(String username);
}
