package io.student.rococo.service.db;

import io.student.rococo.config.Config;
import io.student.rococo.data.entity.data.CountryEntity;
import io.student.rococo.data.entity.data.MuseumEntity;
import io.student.rococo.data.repository.data.MuseumRepository;
import io.student.rococo.data.tpl.XaTransactionTemplate;
import io.student.rococo.model.CountryJson;
import io.student.rococo.model.GeoJson;
import io.student.rococo.model.MuseumJson;
import io.student.rococo.service.MuseumClient;
import org.jetbrains.annotations.NotNull;

import static io.student.rococo.model.MuseumJson.fromEntity;
import static io.student.rococo.utils.Base64Utils.decodeImageFromB64ToBytes;
import static java.util.Objects.requireNonNull;

public class MuseumDbClient implements MuseumClient {
    private static final Config CFG = Config.getInstance();
    private final MuseumRepository museumRepository = new MuseumRepository();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.dataJdbcUrl()
    );

    @NotNull
    @Override
    public MuseumJson createMuseum(MuseumJson museumJson) {
        return requireNonNull(xaTransactionTemplate.execute(() -> {
            CountryEntity country = new CountryEntity();
            country.setName(museumJson.geo().country().name());
            country.setId(museumJson.geo().country().id());

            MuseumEntity museumEntity = new MuseumEntity();
            museumEntity.setTitle(museumJson.title());
            museumEntity.setDescription(museumJson.description());
            museumEntity.setPhoto(museumJson.photo() == null || museumJson.photo().isBlank()
                    ? null
                    : decodeImageFromB64ToBytes(museumJson.photo()));
            museumEntity.setCity(museumJson.geo().city());
            museumEntity.setCountry(country);
            return fromEntity(museumRepository.createMuseum(museumEntity));
        }));
    }

    @NotNull
    @Override
    public MuseumJson createOrUpdateMuseum(MuseumJson museumJson) {
        return requireNonNull(xaTransactionTemplate.execute(() -> {
            CountryEntity country = new CountryEntity();
            country.setName(museumJson.geo().country().name());
            country.setId(museumJson.geo().country().id());

            MuseumEntity museumEntity = new MuseumEntity();
            museumEntity.setTitle(museumJson.title());
            museumEntity.setDescription(museumJson.description());
            museumEntity.setPhoto(museumJson.photo() == null || museumJson.photo().isBlank()
                    ? null
                    : decodeImageFromB64ToBytes(museumJson.photo()));
            museumEntity.setCity(museumJson.geo().city());
            museumEntity.setCountry(country);

            museumRepository.findByTitle(museumEntity.getTitle())
                    .ifPresent(existingMuseum -> museumEntity.setId(existingMuseum.getId()));

            if (museumEntity.getId() != null) {
                return fromEntity(museumRepository.updateMuseum(museumEntity));
            }

            return fromEntity(museumRepository.createMuseum(museumEntity));
        }));
    }

    @NotNull
    @Override
    public MuseumJson updateMuseum(MuseumJson museumJson) {
        return requireNonNull(xaTransactionTemplate.execute(() ->
                fromEntity(museumRepository.updateMuseum(MuseumEntity.fromJson(museumJson)))
        ));
    }
}