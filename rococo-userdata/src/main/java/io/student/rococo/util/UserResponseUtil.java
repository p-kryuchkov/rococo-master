package io.student.rococo.util;

import com.google.protobuf.ByteString;
import io.student.rococo.data.entity.UserEntity;
import io.student.rococo.grpc.UserResponse;

public class UserResponseUtil {
    public static UserResponse userEntityToUserProtoResponse(UserEntity userEntity) {
        return UserResponse.newBuilder()
                .setId(userEntity.getId().toString())
                .setUsername(userEntity.getUsername())
                .setFirstname(userEntity.getFirstname())
                .setLastname(userEntity.getLastname())
                .setAvatar(null == userEntity.getAvatar()
                        ? ByteString.EMPTY
                        : ByteString.copyFrom(userEntity.getAvatar()))
                .build();
    }
}
