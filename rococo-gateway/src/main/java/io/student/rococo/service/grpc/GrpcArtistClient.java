package io.student.rococo.service.grpc;

import com.google.protobuf.ByteString;
import io.grpc.StatusRuntimeException;
import io.student.rococo.exception.GrpcStatusException;
import io.student.rococo.grpc.*;
import io.student.rococo.model.ArtistJson;
import io.student.rococo.model.EventJson;
import io.student.rococo.utils.CurrentUserProvider;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static io.student.rococo.model.EventType.*;
import static io.student.rococo.utils.Base64Utils.decodeImageFromB64ToBytes;
import static io.student.rococo.utils.GrpcUtils.springPageableToGrpcPageableRequest;

@Component
public class GrpcArtistClient {
    private final KafkaTemplate<String, EventJson> kafkaTemplate;
    private final CurrentUserProvider currentUserProvider;

    @GrpcClient("grpcDataClient")
    private ArtistServiceGrpc.ArtistServiceBlockingStub stub;

    @Autowired
    public GrpcArtistClient(KafkaTemplate<String, EventJson>  kafkaTemplate, CurrentUserProvider currentUserProvider) {
        this.kafkaTemplate = kafkaTemplate;
        this.currentUserProvider = currentUserProvider;
    }

    public ArtistJson getArtistById(UUID id) {
        try {
            if (id == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Artist id is required");
            }

            IdRequest idRequest = IdRequest.newBuilder()
                    .setId(id.toString())
                    .build();

            ArtistJson result = ArtistJson.fromGrpcMessage(stub.getArtistById(idRequest));
            kafkaTemplate.send("events",
                    new EventJson(Instant.now(),
                            GET,
                            "Get artist by ID",
                            id,
                            currentUserProvider.getUsername()));
            return result;
        } catch (StatusRuntimeException e) {
            throw new GrpcStatusException(e);
        }
    }

    public Page<ArtistJson> getAllArtists(Pageable pageable) {
        try {
            PageableRequest pageRequest = springPageableToGrpcPageableRequest(pageable);
            ArtistsResponse response = stub.allArtists(pageRequest);

            List<ArtistJson> list = response.getArtistsList().stream()
                    .map(ArtistJson::fromGrpcMessage)
                    .toList();

            kafkaTemplate.send("events",
                    new EventJson(Instant.now(),
                            GET,
                            "Get all artists",
                            null,
                            currentUserProvider.getUsername()));
            return new PageImpl<>(list, pageable, response.getTotalElements());
        } catch (StatusRuntimeException e) {
            throw new GrpcStatusException(e);
        }
    }

    public ArtistJson createArtist(ArtistJson artist) {
        try {
            CreateArtistRequest.Builder builder = CreateArtistRequest.newBuilder()
                    .setName(artist.name())
                    .setBiography(artist.biography());

            if (artist.photo() != null) {
                builder.setPhoto(ByteString.copyFrom(decodeImageFromB64ToBytes(artist.photo())));
            }

            ArtistJson result = ArtistJson.fromGrpcMessage(stub.createArtist(builder.build()));
            kafkaTemplate.send("events",
                    new EventJson(Instant.now(),
                            CREATE,
                            "Create artist",
                            result.id(),
                            currentUserProvider.getUsername()));
            return result;
        } catch (StatusRuntimeException e) {
            throw new GrpcStatusException(e);
        }
    }

    public ArtistJson updateArtist(ArtistJson artist) {
        try {
            if (artist.id() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Artist id is required");
            }

            UpdateArtistRequest.Builder builder = UpdateArtistRequest.newBuilder()
                    .setId(artist.id().toString())
                    .setName(artist.name())
                    .setBiography(artist.biography());

            if (artist.photo() != null) {
                builder.setPhoto(ByteString.copyFrom(decodeImageFromB64ToBytes(artist.photo())));
            }

            ArtistJson result = ArtistJson.fromGrpcMessage(stub.updateArtist(builder.build()));
            kafkaTemplate.send("events",
                    new EventJson(Instant.now(),
                            UPDATE,
                            "Update artist",
                            result.id(),
                            currentUserProvider.getUsername()));
            return result;
        } catch (StatusRuntimeException e) {
            throw new GrpcStatusException(e);
        }
    }
}