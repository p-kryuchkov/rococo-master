package io.student.rococo.service.api;

import com.fasterxml.jackson.databind.JsonNode;
import io.student.rococo.api.AllureApi;
import io.student.rococo.model.allure.AllureProject;
import io.student.rococo.model.allure.AllureResultFile;
import io.student.rococo.model.allure.SendResultsRequest;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.Assertions;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

import static io.student.rococo.utils.AllureReportUtils.readAllureResultFiles;

public class AllureApiClient extends RestClient {

    private static final String ALLURE_BASE_URL =
            System.getenv().getOrDefault("ALLURE_DOCKER_API", "http://localhost:5050/");
    private static final String PROJECT_ID = "rococo-pavel-kryuchkov";

    private final AllureApi allureApi;

    public AllureApiClient() {
        super(ALLURE_BASE_URL,false, HttpLoggingInterceptor.Level.NONE, null);
        this.allureApi = create(AllureApi.class);
    }

    public void clean() throws IOException {
        allureApi.cleanResults(PROJECT_ID).execute();
    }

    public void createProjectIfNotExist() throws IOException {
        Response<JsonNode> response = allureApi.project(PROJECT_ID).execute();

        if (response.code() == 404) {
            int code = allureApi.createProject(new AllureProject(PROJECT_ID))
                    .execute()
                    .code();

            Assertions.assertEquals(201, code);
        } else {
            Assertions.assertEquals(200, response.code());
        }
    }

    public void sendResults() throws IOException {
        List<AllureResultFile> resultFiles = readAllureResultFiles();

        int code = allureApi.uploadResults(
                        PROJECT_ID,
                        new SendResultsRequest(resultFiles)
                )
                .execute()
                .code();

        Assertions.assertEquals(200, code);
    }

    public void generateReport() throws IOException {
        int code = allureApi.generateReport(
                        PROJECT_ID,
                        System.getenv("HEAD_COMMIT_MESSAGE"),
                        System.getenv("BUILD_URL"),
                        System.getenv("EXECUTION_TYPE")
                )
                .execute()
                .code();

        Assertions.assertEquals(200, code);
    }
}