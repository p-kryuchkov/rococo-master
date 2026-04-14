package io.student.rococo.service.api;


import io.student.rococo.api.AllureApi;
import io.student.rococo.model.allure.AllureResultFile;
import io.student.rococo.model.allure.CreateProjectRequest;
import io.student.rococo.model.allure.SendResultsRequest;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

import static io.student.rococo.utils.AllureReportUtils.readAllureResultFiles;


public class AllureApiClient extends RestClient {
    private final AllureApi allureApi;
    private static final String allureBaseUrl =
            System.getenv().getOrDefault("ALLURE_DOCKER_API", "http://localhost:5050/");
    private static final String PROJECT_ID = "rococo";

    public AllureApiClient() {
        super(allureBaseUrl);
        this.allureApi = create(AllureApi.class);
    }

    public void createProject() {
        CreateProjectRequest request = new CreateProjectRequest(PROJECT_ID);
        try {
            Response<Void> response = allureApi.createProject(request).execute();

            if (!response.isSuccessful()) {
                throw new IllegalStateException(
                                "Failed to create allure project. Code: "
                                + response.code()
                                + ", error: " + getErrorBody(response)
                );
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create allure project", e);
        }
    }

    public void sendResults() {
        List<AllureResultFile> resultFiles = readAllureResultFiles();
        SendResultsRequest request = new SendResultsRequest(resultFiles);
        try {
            Response<Void> response = allureApi.sendResults(PROJECT_ID, true,request).execute();

            if (!response.isSuccessful()) {
                throw new IllegalStateException(
                        "Failed to send allure results. Code: "
                                + response.code()
                                + ", error: " + getErrorBody(response)
                );
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to send allure results", e);
        }
    }

    public void generateReport() {
        try {
            Response<Void> response = allureApi.generateReport(PROJECT_ID).execute();

            if (!response.isSuccessful()) {
                throw new IllegalStateException(
                        "Failed to generate allure report. Code: "
                                + response.code()
                                + ", error: " + getErrorBody(response)
                );
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate allure report", e);
        }
    }

    private static String getErrorBody(Response<?> response) {
        try {
            return response.errorBody() == null ? "" : response.errorBody().string();
        } catch (IOException e) {
            return "Unable to read error body";
        }
    }
}
