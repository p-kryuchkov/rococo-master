package io.student.rococo.service.grpc;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import io.student.rococo.data.entity.ArtistEntity;
import io.student.rococo.grpc.*;
import io.student.rococo.service.db.ArtistDbService;
import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static io.student.rococo.utils.GrpcUtils.grpcPageableRequestToSpringPageRequest;


@GrpcService(interceptors = GlobalGrpcExceptionInterceptor.class)
public class GrpcArtistService extends ArtistServiceGrpc.ArtistServiceImplBase {
    private final ArtistDbService artistDbService;

    @Autowired
    public GrpcArtistService(ArtistDbService artistDbService) {
        this.artistDbService = artistDbService;
    }

    @Override
    public void getArtistById(@Nonnull IdRequest request, @Nonnull StreamObserver<ArtistResponse> responseObserver) {
        ArtistEntity entity = artistDbService.getById(request.getId());

        ArtistResponse response = artistEntityToArtistProtoResponse(entity);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getArtistsByName(@Nonnull ArtistNameRequest request, @Nonnull StreamObserver<ArtistsResponse> responseObserver) {
        PageRequest pageRequest = grpcPageableRequestToSpringPageRequest(request.getPageable());
        Page<ArtistEntity> result = artistDbService.getByName(request.getName(), pageRequest);

        ArtistsResponse response = ArtistsResponse.newBuilder()
                .addAllArtists(result.stream()
                        .map(GrpcArtistService::artistEntityToArtistProtoResponse)
                        .toList())
                .setPage(result.getNumber())
                .setSize(result.getSize())
                .setTotalElements(result.getTotalElements())
                .setTotalPages(result.getTotalPages())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void allArtists(@Nonnull PageableRequest request, @Nonnull StreamObserver<ArtistsResponse> responseObserver) {
        PageRequest pageRequest = grpcPageableRequestToSpringPageRequest(request);
        Page<ArtistEntity> result = artistDbService.getAll(pageRequest);

        ArtistsResponse response = ArtistsResponse.newBuilder()
                .addAllArtists(result.stream()
                        .map(GrpcArtistService::artistEntityToArtistProtoResponse)
                        .toList())
                .setPage(result.getNumber())
                .setSize(result.getSize())
                .setTotalElements(result.getTotalElements())
                .setTotalPages(result.getTotalPages())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void createArtist(@Nonnull CreateArtistRequest request, @Nonnull StreamObserver<ArtistResponse> responseObserver) {
        ArtistEntity result = artistDbService.create(
                request.getName(),
                request.getBiography(),
                request.getPhoto().isEmpty() ? null : request.getPhoto().toByteArray()
        );

        ArtistResponse response = ArtistResponse.newBuilder(
                artistEntityToArtistProtoResponse(result)
        ).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateArtist(@Nonnull UpdateArtistRequest request, @Nonnull StreamObserver<ArtistResponse> responseObserver) {
        ArtistEntity result = artistDbService.update(
                request.getId(),
                request.hasName() ? request.getName() : null,
                request.hasBiography() ? request.getBiography() : null,
                request.hasPhoto() ? request.getPhoto().toByteArray() : null
        );

        ArtistResponse response = ArtistResponse.newBuilder(
                artistEntityToArtistProtoResponse(result)
        ).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Nonnull
    public static ArtistResponse artistEntityToArtistProtoResponse(@Nonnull ArtistEntity artistEntity) {
        return ArtistResponse.newBuilder()
                .setId(artistEntity.getId() != null ? artistEntity.getId().toString() : "")
                .setName(artistEntity.getName() != null ? artistEntity.getName() : "")
                .setBiography(artistEntity.getBiography() != null ? artistEntity.getBiography() : "")
                .setPhoto(artistEntity.getPhoto() == null
                        ? ByteString.EMPTY
                        : ByteString.copyFrom(artistEntity.getPhoto()))
                .build();
    }
}