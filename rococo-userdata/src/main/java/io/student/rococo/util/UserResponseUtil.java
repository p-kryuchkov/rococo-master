package io.student.rococo.util;

import com.google.protobuf.ByteString;
import io.student.rococo.data.entity.UserDataEntity;
import io.student.rococo.grpc.UserResponse;
import jakarta.annotation.Nonnull;

public class UserResponseUtil {
    @Nonnull
    public static UserResponse userEntityToUserProtoResponse(@Nonnull UserDataEntity userEntity) {
        return UserResponse.newBuilder()
                .setId(userEntity.getId().toString())
                .setUsername(userEntity.getUsername())
                .setFirstname(userEntity.getFirstname() == null ? "" : userEntity.getFirstname())
                .setLastname(userEntity.getLastname() == null ? "" : userEntity.getLastname())
                .setAvatar(userEntity.getAvatar() == null
                        ? ByteString.EMPTY
                        : ByteString.copyFrom(userEntity.getAvatar()))
                .build();
    }
}
