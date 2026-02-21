package io.student.rococo.service.grpc;

import com.google.protobuf.ByteString;
import io.grpc.StatusRuntimeException;
import io.student.rococo.grpc.ArtistServiceGrpc;
import io.student.rococo.grpc.ArtistsResponse;
import io.student.rococo.grpc.CreateArtistRequest;
import io.student.rococo.grpc.UpdateArtistRequest;
import io.student.rococo.model.ArtistJson;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static io.student.rococo.utils.Base64Utils.decodeImageFromB64ToBytes;

@Component
public class GrpcArtistClient {
    @GrpcClient("grpcArtistClient")
    private ArtistServiceGrpc.ArtistServiceBlockingStub grpcArtistServiceBlockingStub;

    public Page<ArtistJson> getAllArtists(Pageable pageable) {
        try {
            io.student.rococo.grpc.PageableRequest pageRequest = io.student.rococo.grpc.PageableRequest.newBuilder()
                    .setPage(pageable.getPageNumber())
                    .setSize(pageable.getPageSize())
                    .build();
            ArtistsResponse response = grpcArtistServiceBlockingStub.allArtists(pageRequest);
            List<ArtistJson> artistJsonList = response
                    .getArtistsList()
                    .stream()
                    .map(ArtistJson::fromGrpcMessage)
                    .toList();
            return new PageImpl<>(artistJsonList, pageable, response.getTotalElements());
        } catch (StatusRuntimeException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    public ArtistJson createArtist(ArtistJson artist) {
        try {
            CreateArtistRequest.Builder builder = CreateArtistRequest.newBuilder()
                    .setName(artist.name())
                    .setBiography(artist.biography());

            if (artist.photo() != null) {
                builder.setPhoto(
                        ByteString.copyFrom(decodeImageFromB64ToBytes(artist.photo()))
                );
            }

            CreateArtistRequest request = builder.build();
            return ArtistJson.fromGrpcMessage(grpcArtistServiceBlockingStub.createArtist(request));
        } catch (StatusRuntimeException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    public ArtistJson updateArtist(ArtistJson artist) {
        try {
            UpdateArtistRequest.Builder builder = UpdateArtistRequest.newBuilder()
                    .setId(artist.id().toString())
                    .setName(artist.name())
                    .setBiography(artist.biography());

            if (artist.photo() != null) {
                builder.setPhoto(
                        ByteString.copyFrom(decodeImageFromB64ToBytes(artist.photo()))
                );
            }

            UpdateArtistRequest request = builder.build();
            return ArtistJson.fromGrpcMessage(grpcArtistServiceBlockingStub.updateArtist(request));
        } catch (StatusRuntimeException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }
}


