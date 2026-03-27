package io.student.rococo.config;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public ErrorAttributes errorAttributes(){
        return new io.student.rococo.exception.ErrorAttributes();
    }
}
