package io.student.rococo.service.db;

import io.student.rococo.data.repository.UserRepository;
import io.student.rococo.exception.ArtistNotFoundException;
import io.student.rococo.model.UserJson;
import io.student.rococo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDbService implements UserService {
private final UserRepository userRepository;
    @Autowired

    public UserDbService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserJson getUserByUsername(String username) {
        return UserJson.fromEntity(userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException(
                "User not found, username=" + username
        )));
    }
}
