package io.student.rococo.model.allure;

import java.util.List;

public record SendResultsRequest(List<AllureResultFile> results
) {
}