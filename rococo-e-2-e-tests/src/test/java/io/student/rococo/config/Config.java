package io.student.rococo.config;

public interface Config {
    static Config getInstance() {

        return "docker".equals(System.getProperty("test.env"))
                ? DockerConfig.INSTANCE
                : LocalConfig.INSTANCE;
    }
    String frontUrl();

    String authUrl();

    String authJdbcUrl();

    String gatewayUrl();

    String userdataUrl();

    String userdataJdbcUrl();

    String userdataGrpcAddress();

    default int userdataGrpcPort(){
        return 8094;
    };

    String dataUrl();

    String dataJdbcUrl();

    String dataGrpcAddress();

    default int dataGrpcPort(){
        return 8093;
    };

    String eventsJdbcUrl();

    String githubUrl();
}
