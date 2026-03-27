package io.student.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserJson(
    @JsonProperty("username")
    String username) {

}
