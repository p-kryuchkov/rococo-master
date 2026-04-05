package io.student.rococo.model;
import java.util.UUID;

public record UserJson(UUID id,
                       String username,
                       String firstname,
                       String lastname,
                       String avatar) {
}
