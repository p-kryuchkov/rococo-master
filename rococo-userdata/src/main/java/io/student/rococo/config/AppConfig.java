package io.student.rococo.config;

import io.grpc.ServerInterceptor;
import io.student.rococo.service.grpc.GlobalGrpcExceptionInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public ServerInterceptor globalInterceptor() {
        return new GlobalGrpcExceptionInterceptor();
    }
}
