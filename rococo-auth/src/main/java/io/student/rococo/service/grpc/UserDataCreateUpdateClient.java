package io.student.rococo.service.grpc;

import io.grpc.StatusRuntimeException;
import io.student.rococo.grpc.CreateUserRequest;
import io.student.rococo.grpc.UserdataCreateUpdateServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class UserDataCreateUpdateClient {
    //ToDo перепиши под кафку
    @GrpcClient("grpcUserdataCreateUpdateClient")
    private UserdataCreateUpdateServiceGrpc.UserdataCreateUpdateServiceBlockingStub stub;

    public void createUser(String username) {
        try {
            if (username == null || username.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username is required");
            }
            CreateUserRequest.Builder builder = CreateUserRequest.newBuilder()
                    .setUsername(username);
            stub.createUser(builder.build());
        } catch (StatusRuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}