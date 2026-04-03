package io.student.rococo.service.db;

import io.student.rococo.data.entity.UserDataEntity;
import io.student.rococo.data.repository.UserRepository;
import io.student.rococo.exception.FieldValidationException;
import io.student.rococo.exception.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserdataDbService {

    private final UserRepository userRepository;

    public UserdataDbService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserDataEntity getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found by username: " + username));
    }

    @Transactional
    public void createUser(String username, String firstname, String lastname, byte[] avatar) {

        if (username == null) {
            throw new FieldValidationException("Username must not be null");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new FieldValidationException("This username exists: " + username);
        }

        UserDataEntity userEntity = new UserDataEntity();
        userEntity.setUsername(username);
        userEntity.setFirstname(firstname);
        userEntity.setLastname(lastname);

        if (avatar != null) {
            userEntity.setAvatar(avatar);
        }

        userRepository.save(userEntity);
    }

    @Transactional
    public UserDataEntity updateUser(UUID id, String username, String firstname, String lastname, byte[] avatar) {
        UserDataEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found by id: " + id));
        if (username != null) {
            if (!username.equals(userEntity.getUsername())) {
                throw new FieldValidationException("Username cannot be changed");
            }
        }
        if (firstname != null) {
            userEntity.setFirstname(firstname);
        }
        if (lastname != null) {
            userEntity.setLastname(lastname);
        }
        if (avatar != null) {
            userEntity.setAvatar(avatar);
        }
        return userRepository.save(userEntity);
    }
}