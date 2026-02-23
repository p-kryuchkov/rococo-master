package io.student.rococo.exception;

import io.grpc.StatusRuntimeException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;


@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleGrpcException(ResponseStatusException exception, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) {

        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        String message = exception.getReason() != null ? exception.getReason() : status.getReasonPhrase();
        LOG.error(exception.getMessage());
        exception.printStackTrace();
        ApiError apiError = new ApiError(
                status.toString(),
                message,
                httpServletRequest.getRequestURI(),
                exception.getClass().getSimpleName()
        );
        return ResponseEntity.status(status).body(apiError);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntimeException(RuntimeException exception, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) {
        LOG.error(exception.getMessage());
        exception.printStackTrace();
        return new ResponseEntity<>(
                new ApiError(
                        HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                        exception.getMessage(),
                        "GlobalExceptionHandler",
                        exception.getClass().getSimpleName()
                ), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
