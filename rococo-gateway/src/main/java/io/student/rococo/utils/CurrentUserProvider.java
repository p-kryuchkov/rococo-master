package io.student.rococo.utils;

import jakarta.annotation.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {
    @Nullable
    public String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
            return null;
        }

        String username = jwtAuthenticationToken.getToken().getClaimAsString("sub");
        if (username != null && !username.isBlank()) {
            return username;
        }
        return jwtAuthenticationToken.getName();
    }
}

