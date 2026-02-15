package io.student.rococo.model;

import io.student.rococo.data.entity.UserEntity;

import java.util.UUID;

import static io.student.rococo.utils.Base64Utils.encodeImageFromBytesToB64;

public record UserJson(UUID id,
                       String username,
                       String firstname,
                       String lastname,
                       String avatar) {
    public static UserJson fromEntity(UserEntity userEntity){
        return new UserJson(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getFirstname(),
                userEntity.getLastname(),
                userEntity.getAvatar()  == null
                        ? null
                        : encodeImageFromBytesToB64(userEntity.getAvatar())
        );
    }
}
