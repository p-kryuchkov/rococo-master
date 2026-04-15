package io.student.rococo.controller;


import io.student.rococo.model.SessionJson;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;


@RestController
@RequestMapping("/api/session")
public class SessionController {

  @Nonnull
  @GetMapping()
  public SessionJson session(@Nullable @AuthenticationPrincipal Jwt principal) {
    if (principal != null) {
      return new SessionJson(
          principal.getClaim("sub"),
          Date.from(principal.getIssuedAt()),
          Date.from(principal.getExpiresAt())
      );
    } else {
      return SessionJson.empty();
    }
  }
}
