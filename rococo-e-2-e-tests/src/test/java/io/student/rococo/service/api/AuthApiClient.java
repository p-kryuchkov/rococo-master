package io.student.rococo.service.api;


import io.student.rococo.api.AuthApi;
import io.student.rococo.api.core.CodeInterceptor;
import io.student.rococo.api.core.ThreadSafeCookieStore;
import io.student.rococo.config.Config;
import retrofit2.Response;

import javax.annotation.Nonnull;
import java.io.IOException;

public class AuthApiClient extends RestClient {
    private final AuthApi authApi;
    private static final Config CFG = Config.getInstance();


    private final String RESPONSE_TYPE = "code";
    private final String CLIENT_ID = "client";
    private final String SCOPE = "openid";
    private final String REDIRECT_URI = CFG.frontUrl() + "authorized";
    private final String CODE_CHALLENGE_METHOD = "S256";
    private final String GRANT_TYPE = "authorization_code";


    public AuthApiClient() {
        super(CFG.authUrl(), true, new CodeInterceptor());
        this.authApi = create(AuthApi.class);
    }

    @Nonnull
    public Response<Void> register(@Nonnull String username, @Nonnull String password) throws IOException {
        authApi.requestRegisterForm().execute();
        return authApi.register(
                username,
                password,
                password,
                ThreadSafeCookieStore.INSTANCE.xsrfCookie()
        ).execute();
    }

    private void authorize(String codeChallenge) throws IOException {
        authApi.authorize(RESPONSE_TYPE,
                        CLIENT_ID,
                        SCOPE,
                        REDIRECT_URI,
                        codeChallenge,
                        CODE_CHALLENGE_METHOD)
                .execute();
    }

    private void login(@Nonnull String username, @Nonnull String password) throws IOException {
        authApi.login(
                        ThreadSafeCookieStore.INSTANCE.xsrfCookie(),
                        username,
                        password
                )
                .execute();
    }

    private String getToken(@Nonnull String code, String codeVerifier) throws IOException {
        return authApi.token(code, REDIRECT_URI, codeVerifier, GRANT_TYPE, CLIENT_ID).execute().body().path("id_token").asText();
    }
}
