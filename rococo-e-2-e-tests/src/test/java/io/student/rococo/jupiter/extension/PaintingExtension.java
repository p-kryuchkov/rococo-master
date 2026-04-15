package io.student.rococo.jupiter.extension;

import io.student.rococo.jupiter.annotation.Painting;
import io.student.rococo.model.ArtistJson;
import io.student.rococo.model.CountryJson;
import io.student.rococo.model.GeoJson;
import io.student.rococo.model.MuseumJson;
import io.student.rococo.model.PaintingJson;
import io.student.rococo.service.PaintingClient;
import io.student.rococo.service.db.ArtistDbClient;
import io.student.rococo.service.db.CountryDbClient;
import io.student.rococo.service.db.MuseumDbClient;
import io.student.rococo.service.db.PaintingDbClient;
import io.student.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Optional;

import static io.student.rococo.jupiter.extension.TestMethodContextExtension.context;

public class PaintingExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(PaintingExtension.class);

    private final PaintingClient paintingClient = new PaintingDbClient();
    private final ArtistDbClient artistClient = new ArtistDbClient();
    private final MuseumDbClient museumClient = new MuseumDbClient();
    private final CountryDbClient countryDbClient = new CountryDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Painting.class)
                .ifPresent(annotation -> context.getStore(NAMESPACE).put(context.getUniqueId(), createPainting(annotation)));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(PaintingJson.class);
    }

    @Override
    public PaintingJson resolveParameter(ParameterContext parameterContext,
                                         ExtensionContext extensionContext) throws ParameterResolutionException {
        return getPainting().orElseThrow();
    }

    public static Optional<PaintingJson> getPainting() {
        final ExtensionContext methodContext = context();
        return Optional.ofNullable(
                methodContext.getStore(NAMESPACE).get(methodContext.getUniqueId(), PaintingJson.class)
        );
    }

    private PaintingJson createPainting(Painting annotation) {
        ArtistJson artist = artistClient.createOrUpdateArtist(
                new ArtistJson(
                        null,
                        annotation.artist().name().isBlank()
                                ? RandomDataUtils.randomName() + " " + RandomDataUtils.randomSurname()
                                : annotation.artist().name(),
                        annotation.artist().biography(),
                        annotation.artist().photo()
                )
        );

        CountryJson country = new CountryJson(
                countryDbClient.findByName(annotation.museum().countryName())
                        .map(result -> result.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Country not found by name")),
                annotation.museum().countryName()
        );

        MuseumJson museum = museumClient.createOrUpdateMuseum(
                new MuseumJson(
                        null,
                        annotation.museum().description(),
                        annotation.museum().title().isBlank()
                        ? RandomDataUtils.randomAirport() + " " + RandomDataUtils.randomAirport()
                        : annotation.title(),
                        annotation.museum().photo(),
                        new GeoJson(
                                annotation.museum().city(),
                                country
                        )
                )
        );

        return paintingClient.createOrUpdatePainting(
                new PaintingJson(
                        null,
                         annotation.description(),
                        annotation.title().isBlank()
                                ? RandomDataUtils.randomSentence(6)
                                : annotation.title(),
                        annotation.content(),
                        artist,
                        museum
                )
        );
    }
}