package io.student.rococo.service.db;

import io.student.rococo.config.Config;
import io.student.rococo.data.entity.data.ArtistEntity;
import io.student.rococo.data.entity.data.MuseumEntity;
import io.student.rococo.data.entity.data.PaintingEntity;
import io.student.rococo.data.repository.data.PaintingRepository;
import io.student.rococo.data.tpl.XaTransactionTemplate;
import io.student.rococo.model.PaintingJson;
import io.student.rococo.service.PaintingClient;
import org.jetbrains.annotations.NotNull;

import static io.student.rococo.model.PaintingJson.fromEntity;
import static io.student.rococo.utils.Base64Utils.decodeImageFromB64ToBytes;
import static java.util.Objects.requireNonNull;

public class PaintingDbClient implements PaintingClient {
    private static final Config CFG = Config.getInstance();
    private final PaintingRepository paintingRepository = new PaintingRepository();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.dataJdbcUrl()
    );

    @NotNull
    @Override
    public PaintingJson createPainting(PaintingJson paintingJson) {
        return requireNonNull(xaTransactionTemplate.execute(() -> {
            PaintingEntity paintingEntity = new PaintingEntity();
            paintingEntity.setTitle(paintingJson.title());
            paintingEntity.setDescription(paintingJson.description());
            paintingEntity.setContent(paintingJson.content() == null || paintingJson.content().isBlank()
                    ? null
                    : decodeImageFromB64ToBytes(paintingJson.content()));
            paintingEntity.setMuseum(MuseumEntity.fromJson(paintingJson.museum()));
            paintingEntity.setArtist(ArtistEntity.fromJson(paintingJson.artist()));
            return fromEntity(paintingRepository.createPainting(paintingEntity));
        }));
    }

    @NotNull
    @Override
    public PaintingJson updatePainting(PaintingJson paintingJson) {
        return requireNonNull(xaTransactionTemplate.execute(() ->
                fromEntity(paintingRepository.updatePainting(PaintingEntity.fromJson(paintingJson)))
        ));
    }
}