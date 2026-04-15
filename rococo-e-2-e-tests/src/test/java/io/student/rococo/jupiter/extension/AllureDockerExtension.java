package io.student.rococo.jupiter.extension;

import io.student.rococo.service.api.AllureApiClient;

import java.io.IOException;

public class AllureDockerExtension implements SuiteExtension {
    private static final AllureApiClient allureApiClient = new AllureApiClient();

    public AllureDockerExtension() {
        System.out.println("AllureDockerExtension constructor");
    }

    @Override
    public void afterSuite() {
        if ("docker".equals(System.getProperty("test.env"))) {
            try {
                allureApiClient.createProjectIfNotExist();
                allureApiClient.clean();
                allureApiClient.sendResults();
                allureApiClient.generateReport();
            } catch (IOException e) {
                throw new RuntimeException("Failed to publish Allure report", e);
            }
        }
    }
}
