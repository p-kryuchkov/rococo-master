package io.student.rococo.grpc;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import io.student.rococo.data.entity.UserEntity;
import io.student.rococo.data.repository.UserRepository;
import io.student.rococo.exception.FieldValidationException;
import io.student.rococo.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

import java.util.UUID;

@GrpcService(interceptors = GlobalGrpcExceptionInterceptor.class)
public class GrpcUserdataService extends UserdataServiceGrpc.UserdataServiceImplBase {
    private final UserRepository userRepository;

    @Autowired
    public GrpcUserdataService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void getUserByUsername(UsernameRequest request, StreamObserver<UserResponse> responseObserver) {
        UserEntity userEntity = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found by username: " + request.getUsername()));
        responseObserver.onNext(userEntityToUserProtoResponse(userEntity));
        responseObserver.onCompleted();
    }

    @Override
    public void createUser(CreateUserRequest request, StreamObserver<UserResponse> responseObserver) {
        UserEntity userEntity = new UserEntity();
        if (!request.hasUsername()) throw new FieldValidationException("Username must not be null");
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new FieldValidationException("This username exists: " + request.getUsername());
        }
        userEntity.setUsername(request.getUsername());
        userEntity.setFirstname(request.getFirstname());
        userEntity.setLastname(request.getLastname());
        if (!request.getAvatar().isEmpty()) userEntity.setAvatar(request.getAvatar().toByteArray());
        userRepository.save(userEntity);
        responseObserver.onNext(userEntityToUserProtoResponse(userEntity));
        responseObserver.onCompleted();
    }

    @Override
    public void updateUser(UpdateUserRequest request, StreamObserver<UserResponse> responseObserver) {
        UserEntity userEntity = userRepository.findById(UUID.fromString(request.getId()))
                .orElseThrow(() -> new UserNotFoundException("User not found by id: " + request.getId()));
        if (!request.hasUsername()) throw new FieldValidationException("Username must not be null");
        if (!request.getUsername().equals(userEntity.getUsername())
                && userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new FieldValidationException("This username exists: " + request.getUsername());
        }
        userEntity.setUsername(request.getUsername());
        userEntity.setFirstname(request.getFirstname());
        userEntity.setLastname(request.getLastname());
        if (!request.getAvatar().isEmpty()) userEntity.setAvatar(request.getAvatar().toByteArray());
        userRepository.save(userEntity);
        responseObserver.onNext(userEntityToUserProtoResponse(userEntity));
        responseObserver.onCompleted();
    }

    private static UserResponse userEntityToUserProtoResponse(UserEntity userEntity) {
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
