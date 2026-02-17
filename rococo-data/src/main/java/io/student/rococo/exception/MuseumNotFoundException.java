package io.student.rococo.exception;

public class MuseumNotFoundException extends RuntimeException{
    public MuseumNotFoundException(String message) {
        super(message);
    }
}
