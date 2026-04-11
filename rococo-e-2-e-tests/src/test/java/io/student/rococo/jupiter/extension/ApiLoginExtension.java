package io.student.rococo.jupiter.extension;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.student.rococo.api.core.ThreadSafeCookieStore;
import io.student.rococo.config.Config;
import io.student.rococo.jupiter.annotation.ApiLogin;
import io.student.rococo.jupiter.annotation.Token;
import io.student.rococo.model.UserJson;
import io.student.rococo.page.MainPage;
import io.student.rococo.service.api.AuthApiClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;

import java.util.Optional;

public class ApiLoginExtension implements BeforeEachCallback, ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(ApiLoginExtension.class);

    private static final Config CFG = Config.getInstance();
    public static final String DEFAULT_PASSWORD = "12345";


    private final AuthApiClient authApiClient = new AuthApiClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ApiLogin.class)
                .ifPresent(apiLogin -> {
                    final UserJson userToLogin;
                    final Optional<UserJson> userFromUserExtension = UserExtension.getUser();

                    if ("".equals(apiLogin.username())) {
                        if (userFromUserExtension.isEmpty()) {
                            throw new IllegalStateException("@User must be present when @ApiLogin is empty");
                        }
                        userToLogin = userFromUserExtension.get();
                    } else {
                        if (userFromUserExtension.isPresent()) {
                            throw new IllegalStateException("@User must not be present when @ApiLogin contains username or password");
                        }

                        final UserJson fakeUser = new UserJson(
                                null,
                                apiLogin.username(),
                                null,
                                null,
                                null,
                                DEFAULT_PASSWORD
                        );

                        UserExtension.setUser(fakeUser);
                        userToLogin = fakeUser;
                    }

                    final String token = authApiClient.apiLogin(
                            userToLogin.username(),
                            userToLogin.password()
                    );

                    setToken(token);
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(String.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), Token.class);
    }

    @Override
    public String resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return getToken();
    }

    public static void setToken(String token) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("token", token);
    }

    public static String getToken() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("token", String.class);
    }

    public static void setCode(String code) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("code", code);
    }

    public static String getCode() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("code", String.class);
    }

    public static Cookie getJsessionIdCookie() {
        return new Cookie(
                "JSESSIONID",
                ThreadSafeCookieStore.INSTANCE.jsessionIdCookie()
        );
    }
}