package io.student.rococo.jupiter.extension;

import io.student.rococo.jupiter.annotation.Museum;
import io.student.rococo.model.ArtistJson;
import io.student.rococo.model.CountryJson;
import io.student.rococo.model.GeoJson;
import io.student.rococo.model.MuseumJson;
import io.student.rococo.service.MuseumClient;
import io.student.rococo.service.db.CountryDbClient;
import io.student.rococo.service.db.MuseumDbClient;
import io.student.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Optional;
import java.util.UUID;

import static io.student.rococo.jupiter.extension.TestMethodContextExtension.context;

public class MuseumExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(MuseumExtension.class);

    private final MuseumClient museumClient = new MuseumDbClient();
    private final CountryDbClient countryDbClient = new CountryDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Museum.class)
                .ifPresent(annotation -> context.getStore(NAMESPACE).put(context.getUniqueId(), createMuseum(annotation)));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(MuseumJson.class);
    }

    @Override
    public MuseumJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return getMuseum().orElseThrow();
    }

    public static Optional<MuseumJson> getMuseum() {
        final ExtensionContext methodContext = context();
        return Optional.ofNullable(
                methodContext.getStore(NAMESPACE).get(methodContext.getUniqueId(), MuseumJson.class)
        );
    }

    private MuseumJson createMuseum(Museum annotation) {
        String title =  annotation.title();

        String description = annotation.description();

        String photo = annotation.photo();

        String city = annotation.city();

        CountryJson country = new CountryJson(
                countryDbClient.findByName(annotation.countryName())
                        .map(result -> result.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Country not found by name")),
                annotation.countryName()
        );

        return museumClient.createMuseum(
                new MuseumJson(
                        null,
                        title,
                        description,
                        photo,
                        new GeoJson(city, country)));
    }


}