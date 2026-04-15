package io.student.rococo.config;

public enum LocalConfig implements Config{
    INSTANCE;

    @Override
    public String frontUrl() {
        return "http://localhost:3000/";
    }

    @Override
    public String authUrl() {
        return "http://localhost:9000/";
    }

    @Override
    public String authJdbcUrl() {
        return "jdbc:mysql://localhost:3306/rococo-auth";
    }

    @Override
    public String gatewayUrl() {
        return "http://localhost:8080/";
    }

    @Override
    public String userdataUrl() {
        return "http://localhost:8084/";
    }

    @Override
    public String userdataJdbcUrl() {
        return "jdbc:mysql://localhost:3306/rococo-userdata";
    }

    @Override
    public String userdataGrpcAddress() {
        return "localhost";
    }

    @Override
    public String dataUrl() {
        return "http://localhost:8083/";
    }

    @Override
    public String dataJdbcUrl() {
        return "jdbc:mysql://localhost:3306/rococo-data";
    }

    @Override
    public String dataGrpcAddress() {
        return "localhost";
    }

    @Override
    public String eventsJdbcUrl() {
        return "jdbc:mysql://localhost:3306/rococo-events";
    }

    @Override
    public String githubUrl() {
        return "https://api.github.com/";
    }
}
