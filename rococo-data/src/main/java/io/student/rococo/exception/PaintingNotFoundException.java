package io.student.rococo.exception;

public class PaintingNotFoundException extends RuntimeException{
    public PaintingNotFoundException(String message) {
        super(message);
    }
}
