package io.student.rococo.jupiter.extension;

import io.student.rococo.jupiter.annotation.User;
import io.student.rococo.model.UserJson;
import io.student.rococo.service.UserClient;

import io.student.rococo.service.db.UserDbClient;
import io.student.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Optional;

import static io.student.rococo.jupiter.extension.TestMethodContextExtension.context;

public class UserExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(UserExtension.class);
    public static final String DEFAULT_PASSWORD = "12345";

    private final UserClient userClient = new UserDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if ("".equals(userAnno.username())) {
                        final String username = RandomDataUtils.randomUsername();
                        final UserJson user = userClient.createUser(username, DEFAULT_PASSWORD);

                        context.getStore(NAMESPACE).put(
                                context.getUniqueId(),
                                user
                        );
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) throws ParameterResolutionException {
        return getUser().orElseThrow();
    }

    public static Optional<UserJson> getUser() {
        final ExtensionContext methodContext = context();
        return Optional.ofNullable(
                methodContext.getStore(NAMESPACE).get(methodContext.getUniqueId(), UserJson.class)
        );
    }

    public static void setUser(UserJson userJson) {
        final ExtensionContext methodContext = context();
        methodContext.getStore(NAMESPACE).put(
                methodContext.getUniqueId(),
                userJson
        );
    }
}