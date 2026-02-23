package io.student.rococo.service.grpc;

import io.grpc.StatusRuntimeException;
import io.student.rococo.exception.GrpcStatusException;
import io.student.rococo.grpc.UserResponse;
import io.student.rococo.grpc.UserdataReadServiceGrpc;
import io.student.rococo.grpc.UsernameRequest;
import io.student.rococo.model.UserJson;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class GrpcUserdataClient {

    @GrpcClient("grpcUserdataReadClient")
    private UserdataReadServiceGrpc.UserdataReadServiceBlockingStub stub;

    public UserJson getUserByUsername(String username) {
        try {
            if (username == null || username.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username is required");
            }

            UsernameRequest request = UsernameRequest.newBuilder()
                    .setUsername(username)
                    .build();

            UserResponse response = stub.getUserByUsername(request);
            return UserJson.fromGrpcMessage(response);

        } catch (StatusRuntimeException e) {
            throw new GrpcStatusException(e);
        }
    }
}