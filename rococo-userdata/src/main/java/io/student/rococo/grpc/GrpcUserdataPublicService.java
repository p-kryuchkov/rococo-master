package io.student.rococo.grpc;

import io.grpc.stub.StreamObserver;
import io.student.rococo.data.entity.UserDataEntity;
import io.student.rococo.data.repository.UserRepository;
import io.student.rococo.exception.FieldValidationException;
import io.student.rococo.exception.UserNotFoundException;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static io.student.rococo.util.UserResponseUtil.userEntityToUserProtoResponse;

@GrpcService(interceptors = GlobalGrpcExceptionInterceptor.class)
public class GrpcUserdataPublicService extends UserdataReadServiceGrpc.UserdataReadServiceImplBase {
    private final UserRepository userRepository;

    @Autowired
    public GrpcUserdataPublicService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
//ToDO напиши слой работы с базой
    @Override
    public void getUserByUsername(UsernameRequest request, StreamObserver<UserResponse> responseObserver) {
        UserDataEntity userEntity = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found by username: " + request.getUsername()));
        responseObserver.onNext(userEntityToUserProtoResponse(userEntity));
        responseObserver.onCompleted();
    }
    @Override
    public void updateUser(UpdateUserRequest request, StreamObserver<UserResponse> responseObserver) {
        UserDataEntity userEntity = userRepository.findById(UUID.fromString(request.getId()))
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
}
