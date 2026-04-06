package io.student.rococo.service.db;

import io.student.rococo.config.Config;
import io.student.rococo.data.entity.data.ArtistEntity;
import io.student.rococo.data.repository.data.ArtistRepository;
import io.student.rococo.data.tpl.XaTransactionTemplate;
import io.student.rococo.model.ArtistJson;
import io.student.rococo.service.ArtistClient;
import org.jetbrains.annotations.NotNull;

import static io.student.rococo.model.ArtistJson.fromEntity;
import static io.student.rococo.utils.Base64Utils.decodeImageFromB64ToBytes;
import static java.util.Objects.requireNonNull;

public class ArtistDbClient implements ArtistClient {
    private static final Config CFG = Config.getInstance();
    private final ArtistRepository artistRepository = new ArtistRepository();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.dataJdbcUrl()
    );

    @NotNull
    @Override
    public ArtistJson createArtist(ArtistJson artistJson) {
        return requireNonNull(xaTransactionTemplate.execute(() -> {
            ArtistEntity artistEntity = new ArtistEntity();
            artistEntity.setName(artistJson.name());
            artistEntity.setBiography(artistJson.biography());
            artistEntity.setPhoto(artistJson.photo() == null || artistJson.photo().isBlank()
                    ? null
                    : decodeImageFromB64ToBytes(artistJson.photo()));
            return fromEntity(artistRepository.createArtist(artistEntity));
        }));
    }

    @NotNull
    @Override
    public ArtistJson updateArtist(ArtistJson artistJson) {
        return requireNonNull(xaTransactionTemplate.execute(() -> {
                    return fromEntity(artistRepository.updateArtist(ArtistEntity.fromJson(artistJson)));
                }
        ));
    }
}
