package io.student.rococo.config;

public enum DockerConfig implements Config {
    INSTANCE;

    @Override
    public String frontUrl() {
        return "http://frontend.rococo.dc/";
    }

    @Override
    public String authUrl() {
        return "http://auth.rococo.dc:9000/";
    }

    @Override
    public String authJdbcUrl() {
        return "jdbc:mysql://rococo-all-db:3306/rococo-auth";
    }

    @Override
    public String gatewayUrl() {
        return "http://gateway.rococo.dc:8080/";
    }

    @Override
    public String userdataUrl() {
        return "http://userdata.rococo.dc:8084/";
    }

    @Override
    public String userdataJdbcUrl() {
        return "jdbc:mysql://rococo-all-db:3306/rococo-userdata";
    }

    @Override
    public String userdataGrpcAddress() {
        return "static://userdata.rococo.dc:8094";
    }

    @Override
    public String dataUrl() {
        return "http://data.rococo.dc:8083/";
    }

    @Override
    public String dataJdbcUrl() {
        return "jdbc:mysql://rococo-all-db:3306/rococo-data";
    }

    @Override
    public String dataGrpcAddress() {
        return "static://data.rococo.dc:8093";
    }

    @Override
    public String eventsJdbcUrl() {
        return "jdbc:mysql://rococo-all-db:3306/rococo-events";
    }

    @Override
    public String githubUrl() {
        return "https://github.com/";
    }
}