package io.student.rococo.service.grpc;

import com.google.protobuf.ByteString;
import io.grpc.StatusRuntimeException;
import io.student.rococo.exception.GrpcStatusException;
import io.student.rococo.grpc.*;
import io.student.rococo.model.EventJson;
import io.student.rococo.model.GeoJson;
import io.student.rococo.model.MuseumJson;
import io.student.rococo.utils.CurrentUserProvider;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
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
public class GrpcMuseumClient {

    @GrpcClient("grpcDataClient")
    private MuseumServiceGrpc.MuseumServiceBlockingStub stub;

    private final KafkaTemplate<String, EventJson> kafkaTemplate;

    private final CurrentUserProvider currentUserProvider;

    @Nonnull
    public Page<MuseumJson> getAllMuseums(@Nonnull Pageable pageable) {
        try {
            PageableRequest request = springPageableToGrpcPageableRequest(pageable);
            MuseumsResponse response = stub.allMuseums(request);

            List<MuseumJson> list = response.getMuseumsList().stream()
                    .map(MuseumJson::fromGrpcMessage)
                    .toList();
            kafkaTemplate.send("events",
                    new EventJson(Instant.now(),
                            GET,
                            "Get all museums",
                            null,
                            currentUserProvider.getUsername()));
            return new PageImpl<>(list, pageable, response.getTotalElements());
        } catch (StatusRuntimeException e) {
            throw new GrpcStatusException(e);
        }
    }

    @Nonnull
    public Page<MuseumJson> getMuseumsByTitle(@Nonnull String title, @Nonnull Pageable pageable) {
        try {
            if (title == null || title.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Museum name is required");
            }

            PageableRequest pageRequest = springPageableToGrpcPageableRequest(pageable);
            MuseumTitleRequest museumTitleRequest = MuseumTitleRequest.newBuilder()
                    .setTitle(title)
                    .setPageable(pageRequest)
                    .build();

            MuseumsResponse response = stub.findMuseumsByName(museumTitleRequest);
            List<MuseumJson> list = response.getMuseumsList().stream()
                    .map(MuseumJson::fromGrpcMessage)
                    .toList();
            kafkaTemplate.send("events",
                    new EventJson(Instant.now(),
                            GET,
                            "Get museums by title",
                            null,
                            currentUserProvider.getUsername()));
            return new PageImpl<>(list, pageable, response.getTotalElements());
        } catch (StatusRuntimeException e) {
            throw new GrpcStatusException(e);
        }
    }

    @Nonnull
    public MuseumJson getMuseumById(@Nonnull UUID id) {
        try {
            if (id == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Museum id is required");
            }

            IdRequest req = IdRequest.newBuilder()
                    .setId(id.toString())
                    .build();

            MuseumResponse resp = stub.findMuseumById(req);
            MuseumJson result = MuseumJson.fromGrpcMessage(resp);
            kafkaTemplate.send("events",
                    new EventJson(Instant.now(),
                            GET,
                            "Get museum by ID",
                            id,
                            currentUserProvider.getUsername()));
            return result;
        } catch (StatusRuntimeException e) {
            throw new GrpcStatusException(e);
        }
    }

    @Nonnull
    public MuseumJson createMuseum(@Nonnull MuseumJson museumJson) {
        try {
            CreateMuseumRequest.Builder builder = CreateMuseumRequest.newBuilder()
                    .setTitle(museumJson.title() == null ? "" : museumJson.title())
                    .setDescription(museumJson.description() == null ? "" : museumJson.description());

            Geo geo = Geo.newBuilder()
                    .setCity(museumJson.geo().city() == null ? "" : museumJson.geo().city())
                    .setCountryId(museumJson.geo().country().id() == null ? "" : museumJson.geo().country().id().toString())
                    .build();
            builder.setGeo(geo);

            if (museumJson.photo() != null) {
                builder.setPhoto(ByteString.copyFrom(decodeImageFromB64ToBytes(museumJson.photo())));
            }

            MuseumResponse resp = stub.createMuseum(builder.build());
            MuseumJson result = MuseumJson.fromGrpcMessage(resp);
            kafkaTemplate.send("events",
                    new EventJson(Instant.now(),
                            CREATE,
                            "Create museum",
                            result.id(),
                            currentUserProvider.getUsername()));
            return result;
        } catch (StatusRuntimeException e) {
            throw new GrpcStatusException(e);
        }
    }

    @Nonnull
    public MuseumJson updateMuseum(@Nonnull MuseumJson museumJson) {
        try {
            if (museumJson.id() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Museum id is required");
            }

            UpdateMuseumRequest.Builder builder = UpdateMuseumRequest.newBuilder()
                    .setId(museumJson.id().toString());

            if (museumJson.title() != null) {
                builder.setTitle(museumJson.title());
            }
            if (museumJson.description() != null) {
                builder.setDescription(museumJson.description());
            }

            GeoJson geo = museumJson.geo();
            if (geo != null) {
                if (geo.city() != null || geo.country().id() != null) {
                    Geo.Builder geoBuilder = Geo.newBuilder();
                    geoBuilder.setCity(geo.city() == null ? "" : geo.city());
                    geoBuilder.setCountryId(geo.country().id() == null ? "" : geo.country().id().toString());
                    builder.setGeo(geoBuilder.build());
                }
            }
            if (museumJson.photo() != null) {
                builder.setPhoto(ByteString.copyFrom(decodeImageFromB64ToBytes(museumJson.photo())));
            }

            MuseumResponse resp = stub.updateMuseum(builder.build());
            MuseumJson result = MuseumJson.fromGrpcMessage(resp);
            kafkaTemplate.send("events",
                    new EventJson(Instant.now(),
                            UPDATE,
                            "Update museum",
                            result.id(),
                            currentUserProvider.getUsername()));
            return result;
        } catch (StatusRuntimeException e) {
            throw new GrpcStatusException(e);
        }
    }
}