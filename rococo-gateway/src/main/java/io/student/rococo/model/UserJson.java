package io.student.rococo.model;
import io.student.rococo.grpc.UserResponse;

import java.util.UUID;

import static io.student.rococo.utils.Base64Utils.encodeImageFromBytesToB64;

public record UserJson(UUID id,
                       String username,
                       String firstname,
                       String lastname,
                       String avatar) {
    public static UserJson fromGrpcMessage(UserResponse grpc) {

        UUID uuid = (grpc.getId().isBlank())
                ? null
                : UUID.fromString(grpc.getId());

        String avatar = grpc.getAvatar().isEmpty()
                ? null
                : encodeImageFromBytesToB64(grpc.getAvatar().toByteArray());

        return new UserJson(
                uuid,
                grpc.getUsername(),
                grpc.getFirstname(),
                grpc.getLastname(),
                avatar
        );
    }
}
