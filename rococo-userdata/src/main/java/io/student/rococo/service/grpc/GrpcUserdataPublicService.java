package io.student.rococo.service.grpc;

import io.grpc.stub.StreamObserver;
import io.student.rococo.data.entity.UserDataEntity;
import io.student.rococo.exception.FieldValidationException;
import io.student.rococo.grpc.UpdateUserRequest;
import io.student.rococo.grpc.UserResponse;
import io.student.rococo.grpc.UserdataReadServiceGrpc;
import io.student.rococo.grpc.UsernameRequest;
import io.student.rococo.service.db.UserdataDbService;
import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.UUID;

import static io.student.rococo.util.UserResponseUtil.userEntityToUserProtoResponse;

@GrpcService(interceptors = GlobalGrpcExceptionInterceptor.class)
public class GrpcUserdataPublicService extends UserdataReadServiceGrpc.UserdataReadServiceImplBase {

    private final UserdataDbService userdataDbService;

    public GrpcUserdataPublicService(UserdataDbService userdataDbService) {
        this.userdataDbService = userdataDbService;
    }

    @Override
    public void getUserByUsername(@Nonnull UsernameRequest request, @Nonnull StreamObserver<UserResponse> responseObserver) {
        UserDataEntity userEntity = userdataDbService.getByUsername(request.getUsername());
        responseObserver.onNext(userEntityToUserProtoResponse(userEntity));
        responseObserver.onCompleted();
    }

    @Override
    public void updateUser(@Nonnull UpdateUserRequest request, @Nonnull StreamObserver<UserResponse> responseObserver) {
        if (!request.hasUsername()) {
            throw new FieldValidationException("Username must not be null");
        }

        byte[] avatar = request.getAvatar().isEmpty()
                ? null
                : request.getAvatar().toByteArray();

        UserDataEntity updated = userdataDbService.updateUser(
                request.getUsername(),
                request.getFirstname(),
                request.getLastname(),
                avatar
        );

        responseObserver.onNext(userEntityToUserProtoResponse(updated));
        responseObserver.onCompleted();
    }
}