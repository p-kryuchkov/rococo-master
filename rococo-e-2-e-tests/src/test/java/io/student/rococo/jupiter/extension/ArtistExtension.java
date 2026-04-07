package io.student.rococo.jupiter.extension;


import io.student.rococo.jupiter.annotation.Artist;
import io.student.rococo.model.ArtistJson;
import io.student.rococo.service.ArtistClient;
import io.student.rococo.service.db.ArtistDbClient;
import io.student.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Optional;

import static io.student.rococo.jupiter.extension.TestMethodContextExtension.context;

public class ArtistExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ArtistExtension.class);

    private final ArtistDbClient artistClient = new ArtistDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Artist.class)
                .ifPresent(annotation -> context.getStore(NAMESPACE).put(context.getUniqueId(), createArtist(annotation)));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(ArtistJson.class);
    }

    @Override
    public ArtistJson resolveParameter(ParameterContext parameterContext,
                                       ExtensionContext extensionContext) throws ParameterResolutionException {
        return getArtist().orElseThrow();
    }

    public static Optional<ArtistJson> getArtist() {
        final ExtensionContext methodContext = context();
        return Optional.ofNullable(
                methodContext.getStore(NAMESPACE).get(methodContext.getUniqueId(), ArtistJson.class)
        );
    }

    private ArtistJson createArtist(Artist annotation) {
        String name = annotation.name();
        String biography = annotation.biography();
        String photo = (annotation.photo());
        return artistClient.createArtist(new ArtistJson(null, name, biography, photo));
    }

}