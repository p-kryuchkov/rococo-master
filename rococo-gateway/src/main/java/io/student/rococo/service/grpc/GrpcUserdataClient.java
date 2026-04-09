package io.student.rococo.service.grpc;

import com.google.protobuf.ByteString;
import io.grpc.StatusRuntimeException;
import io.student.rococo.exception.GrpcStatusException;
import io.student.rococo.grpc.UpdateUserRequest;
import io.student.rococo.grpc.UserResponse;
import io.student.rococo.grpc.UserdataReadServiceGrpc;
import io.student.rococo.grpc.UsernameRequest;
import io.student.rococo.model.EventJson;
import io.student.rococo.model.UserJson;
import io.student.rococo.utils.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

import static io.student.rococo.model.EventType.GET;
import static io.student.rococo.model.EventType.UPDATE;
import static io.student.rococo.utils.Base64Utils.decodeImageFromB64ToBytes;

@Component
@RequiredArgsConstructor
public class GrpcUserdataClient {

    @GrpcClient("grpcUserdataPublicClient")
    private UserdataReadServiceGrpc.UserdataReadServiceBlockingStub stub;

    private final KafkaTemplate<String, EventJson> kafkaTemplate;

    private final CurrentUserProvider currentUserProvider;

    public UserJson getUserByUsername(String username) {
        try {
            if (username == null || username.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username is required");
            }

            UsernameRequest request = UsernameRequest.newBuilder()
                    .setUsername(username)
                    .build();

            UserResponse response = stub.getUserByUsername(request);
            UserJson result = UserJson.fromGrpcMessage(response);
            kafkaTemplate.send("events",
                    new EventJson(Instant.now(),
                            GET,
                            "Get User by Username",
                            result.id(),
                            currentUserProvider.getUsername()));
            return result;

        } catch (StatusRuntimeException e) {
            throw new GrpcStatusException(e);
        }
    }

    public UserJson updateUser(UserJson user) {
        try {
            if (user.username() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required");
            }
            UpdateUserRequest.Builder builder = UpdateUserRequest.newBuilder()
                    .setUsername(user.username());
            if (user.firstname() != null) {
                builder.setFirstname(user.firstname());
            }
            if (user.lastname() != null) {
                builder.setLastname(user.lastname());
            }
            if (user.avatar() != null) {
                builder.setAvatar(ByteString.copyFrom(decodeImageFromB64ToBytes(user.avatar())));
            }

            UserJson result = UserJson.fromGrpcMessage(stub.updateUser(builder.build()));
            kafkaTemplate.send("events",
                    new EventJson(
                            Instant.now(),
                            UPDATE,
                            "Update user",
                            result.id(),
                            currentUserProvider.getUsername()
                    ));
            return result;
        } catch (StatusRuntimeException e) {
            throw new GrpcStatusException(e);
        }
    }
}