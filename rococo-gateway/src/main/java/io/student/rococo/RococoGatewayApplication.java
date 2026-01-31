package io.student.rococo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RococoGatewayApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(RococoGatewayApplication.class);
        springApplication.run(args);
    }
}
