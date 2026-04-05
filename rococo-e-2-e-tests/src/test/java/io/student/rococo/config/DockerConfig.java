package io.student.rococo.config;

public enum DockerConfig implements Config{
    INSTANCE;

    @Override
    public String frontUrl() {
        return "";
    }

    @Override
    public String authUrl() {
        return "";
    }

    @Override
    public String authJdbcUrl() {
        return "";
    }

    @Override
    public String gatewayUrl() {
        return "";
    }

    @Override
    public String userdataUrl() {
        return "";
    }

    @Override
    public String userdataJdbcUrl() {
        return "";
    }

    @Override
    public String userdataGrpcAddress() {
        return "";
    }

    @Override
    public String dataUrl() {
        return "";
    }

    @Override
    public String dataJdbcUrl() {
        return "";
    }

    @Override
    public String dataGrpcAddress() {
        return "";
    }

    @Override
    public String eventsJdbcUrl() {
        return "";
    }

    @Override
    public String githubUrl() {
        return "";
    }
}
