
package io.student.rococo.api;


import com.fasterxml.jackson.databind.JsonNode;
import io.student.rococo.model.allure.AllureProject;
import io.student.rococo.model.allure.AllureResultFile;
import io.student.rococo.model.allure.CreateProjectRequest;
import io.student.rococo.model.allure.SendResultsRequest;
import retrofit2.Call;
import retrofit2.http.*;

public interface AllureApi {
    @POST("allure-docker-service/send-results")
    Call<JsonNode> uploadResults(@Query("project_id") String projectId,
                                 @Body SendResultsRequest request);

    @GET("allure-docker-service/projects/{project_id}")
    Call<JsonNode> project(@Path("project_id") String projectId);

    @GET("allure-docker-service/clean-results")
    Call<JsonNode> cleanResults(@Query("project_id") String projectId);

    @GET("allure-docker-service/generate-report")
    Call<JsonNode> generateReport(@Query("project_id") String projectId,
                                  @Query("execution_name") String executionName,
                                  @Query(value = "execution_from", encoded = true) String executionFrom,
                                  @Query("execution_type") String executionType);

    @POST("allure-docker-service/projects")
    Call<JsonNode> createProject(@Body AllureProject project);
}