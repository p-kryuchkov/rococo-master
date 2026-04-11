package io.student.rococo.model;

import io.student.rococo.data.entity.userdata.UserDataEntity;

import javax.annotation.Nonnull;
import java.util.UUID;

import static io.student.rococo.utils.Base64Utils.encodeImageFromBytesToB64;

public record UserJson(UUID id,
                       String username,
                       String firstname,
                       String lastname,
                       String avatar,
                       String password) {
    public UserJson(UUID id,
             String username,
             String firstname,
             String lastname,
             String avatar) {
        this(id,
                username,
                firstname,
                lastname,
                avatar,
                null);
    }

    public static @Nonnull UserJson fromEntity(@Nonnull final UserDataEntity userEntity) {
        return new UserJson(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getFirstname(),
                userEntity.getLastname(),
                userEntity.getAvatar() == null
                        ? null
                        : encodeImageFromBytesToB64(userEntity.getAvatar())
        );
    }
}
