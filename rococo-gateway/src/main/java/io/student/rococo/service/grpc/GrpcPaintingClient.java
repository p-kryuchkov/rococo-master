package io.student.rococo.service.grpc;

import com.google.protobuf.ByteString;
import io.grpc.StatusRuntimeException;
import io.student.rococo.exception.GrpcStatusException;
import io.student.rococo.grpc.*;
import io.student.rococo.model.EventJson;
import io.student.rococo.model.PaintingJson;
import io.student.rococo.utils.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static io.student.rococo.model.EventType.*;
import static io.student.rococo.utils.Base64Utils.decodeImageFromB64ToBytes;
import static io.student.rococo.utils.GrpcUtils.springPageableToGrpcPageableRequest;

@Component
@RequiredArgsConstructor
public class GrpcPaintingClient {

    @GrpcClient("grpcDataClient")
    private PaintingServiceGrpc.PaintingServiceBlockingStub stub;

    private final KafkaTemplate<String, EventJson> kafkaTemplate;

    private final CurrentUserProvider currentUserProvider;

    public Page<PaintingJson> getAllPaintings(Pageable pageable) {
        try {
            PageableRequest req = springPageableToGrpcPageableRequest(pageable);
            PaintingsResponse resp = stub.allPaintings(req);

            List<PaintingJson> items = resp.getPaintingsList().stream()
                    .map(PaintingJson::fromGrpcMessage)
                    .toList();
            kafkaTemplate.send("events",
                    new EventJson(Instant.now(),
                            GET,
                            "Get All Paintings",
                            null,
                            currentUserProvider.getUsername()));
            return new PageImpl<>(items, pageable, resp.getTotalElements());
        } catch (StatusRuntimeException e) {
            throw new GrpcStatusException(e);
        }
    }

    public PaintingJson getPaintingById(UUID id) {
        try {
            if (id == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Painting id is required");
            }

            IdRequest req = IdRequest.newBuilder()
                    .setId(id.toString())
                    .build();

            PaintingResponse resp = stub.findPaintingById(req);
            PaintingJson result = PaintingJson.fromGrpcMessage(resp);
            kafkaTemplate.send("events",
                    new EventJson(Instant.now(),
                            GET,
                            "Get Painting by Id",
                            result.id(),
                            currentUserProvider.getUsername()));
            return result;
        } catch (StatusRuntimeException e) {
            throw new GrpcStatusException(e);
        }
    }

    public Page<PaintingJson> getPaintingsByArtist(UUID artistId, Pageable pageable) {
        try {
            if (artistId == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Artist id is required");
            }

            PageableRequest pageableReq = springPageableToGrpcPageableRequest(pageable);

            PaintingsByArtistRequest req = PaintingsByArtistRequest.newBuilder()
                    .setArtistId(artistId.toString())
                    .setPageable(pageableReq)
                    .build();

            PaintingsResponse resp = stub.findPaintingByArtist(req);

            List<PaintingJson> items = resp.getPaintingsList().stream()
                    .map(PaintingJson::fromGrpcMessage)
                    .toList();
            kafkaTemplate.send("events",
                    new EventJson(Instant.now(),
                            GET,
                            "Get Paintings by Artist",
                            artistId,
                            currentUserProvider.getUsername()));

            return new PageImpl<>(items, pageable, resp.getTotalElements());
        } catch (StatusRuntimeException e) {
            throw new GrpcStatusException(e);
        }
    }

    public PaintingJson createPainting(PaintingJson paintingJson) {
        try {
            if (paintingJson == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Painting payload is required");
            }

            CreatePaintingRequest.Builder builder = CreatePaintingRequest.newBuilder()
                    .setTitle(paintingJson.title() == null ? "" : paintingJson.title())
                    .setDescription(paintingJson.description() == null ? "" : paintingJson.description());

            if (paintingJson.content() != null) {
                builder.setContent(ByteString.copyFrom(decodeImageFromB64ToBytes(paintingJson.content())));
            }

            // artist/museum — у тебя в JSON это объекты
            if (paintingJson.artist() != null && paintingJson.artist().id() != null) {
                builder.setArtist(ArtistRequest.newBuilder()
                        .setId(paintingJson.artist().id().toString())
                        .build());
            }

            if (paintingJson.museum() != null && paintingJson.museum().id() != null) {
                builder.setMuseum(MuseumRequest.newBuilder()
                        .setId(paintingJson.museum().id().toString())
                        .build());
            }

            PaintingResponse resp = stub.createPainting(builder.build());
            PaintingJson result = PaintingJson.fromGrpcMessage(resp);
            kafkaTemplate.send("events",
                    new EventJson(Instant.now(),
                            CREATE,
                            "Create Painting",
                            result.id(),
                            currentUserProvider.getUsername()));
            return result;
        } catch (StatusRuntimeException e) {
            throw new GrpcStatusException(e);
        }
    }

    public PaintingJson updatePainting(PaintingJson paintingJson) {
        try {
            if (paintingJson.id() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Painting id is required");
            }

            UpdatePaintingRequest.Builder builder = UpdatePaintingRequest.newBuilder()
                    .setId(paintingJson.id().toString());

            if (paintingJson.title() != null) {
                builder.setTitle(paintingJson.title());
            }
            if (paintingJson.description() != null) {
                builder.setDescription(paintingJson.description());
            }
            if (paintingJson.content() != null) {
                builder.setContent(ByteString.copyFrom(decodeImageFromB64ToBytes(paintingJson.content())));
            }

            if (paintingJson.artist() != null && paintingJson.artist().id() != null) {
                builder.setArtist(ArtistRequest.newBuilder()
                        .setId(paintingJson.artist().id().toString())
                        .build());
            }

            if (paintingJson.museum() != null && paintingJson.museum().id() != null) {
                builder.setMuseum(MuseumRequest.newBuilder()
                        .setId(paintingJson.museum().id().toString())
                        .build());
            }

            PaintingResponse resp = stub.updatePainting(builder.build());
            PaintingJson result = PaintingJson.fromGrpcMessage(resp);
            kafkaTemplate.send("events",
                    new EventJson(Instant.now(),
                            UPDATE,
                            "Update Painting",
                            result.id(),
                            currentUserProvider.getUsername()));
            return result;
        } catch (StatusRuntimeException e) {
            throw new GrpcStatusException(e);
        }
    }
}