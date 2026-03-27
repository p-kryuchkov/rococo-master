package io.student.rococo.config;

import io.student.rococo.service.cors.CorsCustomizer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.http.HttpMethod.GET;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
@Profile({"local", "docker"})
public class SecurityConfigLocal {
    private final CorsCustomizer corsCustomizer;
    @Autowired
    public SecurityConfigLocal(CorsCustomizer corsCustomizer) {
        this.corsCustomizer = corsCustomizer;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        corsCustomizer.corsCustomizer(http);
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(customizer ->
                        customizer
                                .requestMatchers(
                                        "/api/session/current",
                                        "/actuator/health").permitAll()
                                .requestMatchers(GET,"/api/session").permitAll()
                                .requestMatchers(GET, "/api/artist/**").permitAll()
                                .requestMatchers(GET, "/api/museum/**").permitAll()
                                .requestMatchers(GET, "/api/painting/**").permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}