package io.student.rococo.service.api;


import io.student.rococo.api.AuthApi;
import io.student.rococo.api.core.CodeInterceptor;
import io.student.rococo.api.core.ThreadSafeCookieStore;
import io.student.rococo.config.Config;
import io.student.rococo.jupiter.extension.ApiLoginExtension;
import okhttp3.ResponseBody;
import retrofit2.Response;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.student.rococo.utils.OauthUtils.generateCodeChallenge;
import static io.student.rococo.utils.OauthUtils.generateCodeVerifier;

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
    public String apiLogin(@Nonnull String username, @Nonnull String password) {
        ThreadSafeCookieStore.INSTANCE.removeAll();
        ApiLoginExtension.setCode(null);
        final String codeVerifier = generateCodeVerifier();
        final String codeChallenge = generateCodeChallenge(codeVerifier);
        try {
            authorize(codeChallenge);
            login(username, password);
            return getToken(ApiLoginExtension.getCode(), codeVerifier);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Nonnull
    public Response<Void> register(@Nonnull String username, @Nonnull String password) throws IOException {
        final Response<ResponseBody> formResponse = authApi.requestRegisterForm().execute();

        if (!formResponse.isSuccessful() || formResponse.body() == null) {
            throw new IOException("Failed to load register page");
        }
        final String html = formResponse.body().string();
        addCsrfFromHtml(html);
        return authApi.register(
                username,
                password,
                password,
                ThreadSafeCookieStore.INSTANCE.xsrfCookie()
        ).execute();
    }

    private void authorize(String codeChallenge) throws IOException {
        final Response<ResponseBody> response = authApi.authorize(
                RESPONSE_TYPE,
                CLIENT_ID,
                SCOPE,
                REDIRECT_URI,
                codeChallenge,
                CODE_CHALLENGE_METHOD
        ).execute();

        if (!response.isSuccessful() || response.body() == null) {
            throw new IOException("Failed to load login page");
        }

        final String html = response.body().string();
        addCsrfFromHtml(html);
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

    @Nonnull
    private void addCsrfFromHtml(@Nonnull String html) {
        final Pattern csrfPattern =
                Pattern.compile("name=\"_csrf\"\\s+value=\"([^\"]+)\"");
        final Matcher matcher = csrfPattern.matcher(html);
        if (!matcher.find()) {
            throw new IllegalStateException("CSRF token not found in register page");
        }
        try {
            ThreadSafeCookieStore.INSTANCE.add(new URI(CFG.authUrl()),new HttpCookie( "XSRF-TOKEN",matcher.group(1)));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
