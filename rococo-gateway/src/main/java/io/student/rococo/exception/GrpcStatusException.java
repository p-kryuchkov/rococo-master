package io.student.rococo.exception;

import io.grpc.StatusRuntimeException;
import org.springframework.web.server.ResponseStatusException;

public class GrpcStatusException extends ResponseStatusException {

    private final StatusRuntimeException grpcException;

    public GrpcStatusException(StatusRuntimeException e) {
        super(
                GrpcStatusMapper.toHttpStatus(e),
                GrpcStatusMapper.toMessage(e),
                e
        );
        this.grpcException = e;
    }

    public StatusRuntimeException getGrpcException() {
        return grpcException;
    }
}