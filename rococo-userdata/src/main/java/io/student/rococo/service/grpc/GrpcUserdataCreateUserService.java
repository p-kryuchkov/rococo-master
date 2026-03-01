package io.student.rococo.service.grpc;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import io.student.rococo.exception.FieldValidationException;
import io.student.rococo.grpc.CreateUserRequest;
import io.student.rococo.grpc.UserdataCreateUserServiceGrpc;
import io.student.rococo.service.db.UserdataDbService;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService(interceptors = GlobalGrpcExceptionInterceptor.class)
public class GrpcUserdataCreateUserService
        extends UserdataCreateUserServiceGrpc.UserdataCreateUserServiceImplBase {

    private final UserdataDbService userdataDbService;

    public GrpcUserdataCreateUserService(UserdataDbService userdataDbService) {
        this.userdataDbService = userdataDbService;
    }

    @Override
    public void createUser(CreateUserRequest request,
                           StreamObserver<Empty> responseObserver) {

        if (!request.hasUsername()) {
            throw new FieldValidationException("Username must not be null");
        }

        byte[] avatarOrNull = request.getAvatar().isEmpty()
                ? null
                : request.getAvatar().toByteArray();

        userdataDbService.createUser(request.getUsername(), request.getFirstname(), request.getLastname(), avatarOrNull);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}