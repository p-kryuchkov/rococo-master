package io.student.rococo.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleException(RuntimeException exception, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) {
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
