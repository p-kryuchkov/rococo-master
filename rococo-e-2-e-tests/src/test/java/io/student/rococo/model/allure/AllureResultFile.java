package io.student.rococo.model.allure;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AllureResultFile(@JsonProperty("file_name") String fileName,
                               @JsonProperty("content_base64") String contentBase64) {
}