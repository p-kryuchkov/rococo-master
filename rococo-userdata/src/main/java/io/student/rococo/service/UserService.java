package io.student.rococo.service;

import io.student.rococo.model.UserJson;

public interface UserService {
    public UserJson getUserByUsername(String username);
}
