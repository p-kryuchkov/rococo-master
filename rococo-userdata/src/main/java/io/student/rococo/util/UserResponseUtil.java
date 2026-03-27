package io.student.rococo.util;

import com.google.protobuf.ByteString;
import io.student.rococo.data.entity.UserDataEntity;
import io.student.rococo.grpc.UserResponse;

public class UserResponseUtil {
    public static UserResponse userEntityToUserProtoResponse(UserDataEntity userEntity) {
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
