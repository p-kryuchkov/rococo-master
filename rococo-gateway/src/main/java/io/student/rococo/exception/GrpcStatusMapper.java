package io.student.rococo.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.http.HttpStatus;

import static io.grpc.Status.Code.*;

public final class GrpcStatusMapper {

    private GrpcStatusMapper() {}

    public static HttpStatus toHttpStatus(StatusRuntimeException e) {
        Status.Code code = e.getStatus().getCode();
        return switch (code) {
            case INVALID_ARGUMENT -> HttpStatus.BAD_REQUEST;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case FAILED_PRECONDITION -> HttpStatus.PRECONDITION_FAILED;
            case PERMISSION_DENIED -> HttpStatus.FORBIDDEN;
            case UNAUTHENTICATED -> HttpStatus.UNAUTHORIZED;
            case DEADLINE_EXCEEDED -> HttpStatus.GATEWAY_TIMEOUT;
            case UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
            case CANCELLED -> HttpStatus.SERVICE_UNAVAILABLE;
            default -> HttpStatus.BAD_GATEWAY;
        };
    }

    public static String toMessage(StatusRuntimeException e) {
        String desc = e.getStatus().getDescription();
        if (desc != null && !desc.isBlank()) return desc;
        return "gRPC error: " + e.getStatus().getCode().name();
    }
}