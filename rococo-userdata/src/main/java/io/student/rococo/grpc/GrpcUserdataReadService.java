package io.student.rococo.grpc;

import io.grpc.stub.StreamObserver;
import io.student.rococo.data.entity.UserEntity;
import io.student.rococo.data.repository.UserRepository;
import io.student.rococo.exception.UserNotFoundException;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import static io.student.rococo.util.UserResponseUtil.userEntityToUserProtoResponse;

@GrpcService(interceptors = GlobalGrpcExceptionInterceptor.class)
public class GrpcUserdataReadService extends UserdataReadServiceGrpc.UserdataReadServiceImplBase {
    private final UserRepository userRepository;

    @Autowired
    public GrpcUserdataReadService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
//ToDO напиши слой работы с базой
    @Override
    public void getUserByUsername(UsernameRequest request, StreamObserver<UserResponse> responseObserver) {
        UserEntity userEntity = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found by username: " + request.getUsername()));
        responseObserver.onNext(userEntityToUserProtoResponse(userEntity));
        responseObserver.onCompleted();
    }
}
