package io.student.rococo.service.grpc;

import com.google.protobuf.ByteString;
import io.grpc.StatusRuntimeException;
import io.student.rococo.exception.GrpcStatusException;
import io.student.rococo.grpc.*;
import io.student.rococo.model.ArtistJson;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static io.student.rococo.utils.Base64Utils.decodeImageFromB64ToBytes;
import static io.student.rococo.utils.GrpcUtils.springPageableToGrpcPageableRequest;

@Component
public class GrpcArtistClient {

    @GrpcClient("grpcDataClient")
    private ArtistServiceGrpc.ArtistServiceBlockingStub stub;

    public ArtistJson getArtistById(UUID id) {
        try {
            IdRequest idRequest = IdRequest.newBuilder()
                    .setId(id.toString())
                    .build();
            return ArtistJson.fromGrpcMessage(stub.getArtistById(idRequest));
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

            return ArtistJson.fromGrpcMessage(stub.createArtist(builder.build()));
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

            return ArtistJson.fromGrpcMessage(stub.updateArtist(builder.build()));
        } catch (StatusRuntimeException e) {
            throw new GrpcStatusException(e);
        }
    }
}