package io.student.rococo.controller;

import io.student.rococo.model.UserJson;
import io.student.rococo.service.grpc.GrpcUserdataClient;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
public class UserController {
    private final GrpcUserdataClient userdataClient;

    @Autowired
    public UserController(GrpcUserdataClient userdataClient) {
        this.userdataClient = userdataClient;
    }

    @Nonnull
    @GetMapping
    public UserJson getCurrentUser(@AuthenticationPrincipal @Nonnull Jwt principal) {
        return userdataClient.getUserByUsername(principal.getClaim("sub"));
    }

    @Nonnull
    @PatchMapping
    public UserJson updateUser(@RequestBody @Nonnull UserJson user) {
        return userdataClient.updateUser(user);
    }
}
