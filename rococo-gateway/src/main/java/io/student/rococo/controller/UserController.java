package io.student.rococo.controller;

import io.student.rococo.model.UserJson;
import io.student.rococo.service.grpc.GrpcUserdataClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user")
public class UserController {
    private final GrpcUserdataClient userdataClient;

    @Autowired
    public UserController(GrpcUserdataClient userdataClient) {
        this.userdataClient = userdataClient;
    }

    @GetMapping
    public UserJson getCurrentUser(@AuthenticationPrincipal Jwt principal) {
        return userdataClient.getUserByUsername(principal.getClaim("sub"));
    }
}
