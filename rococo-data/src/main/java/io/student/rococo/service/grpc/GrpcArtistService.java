package io.student.rococo.service.grpc;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import io.student.rococo.data.entity.ArtistEntity;
import io.student.rococo.grpc.*;
import io.student.rococo.service.db.ArtistDbService;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


@GrpcService(interceptors = GlobalGrpcExceptionInterceptor.class)
public class GrpcArtistService extends ArtistServiceGrpc.ArtistServiceImplBase {
    private final ArtistDbService artistDbService;

    @Autowired
    public GrpcArtistService(ArtistDbService artistDbService) {
        this.artistDbService = artistDbService;
    }

    @Override
    public void getAristById(IdRequest request, StreamObserver<ArtistsResponse> responseObserver) {
        ArtistEntity entity = artistDbService.getById(request.getId());

        ArtistsResponse response = ArtistsResponse.newBuilder()
                .addArtists(artistEntityToArtistProtoResponse(entity))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void allArtists(PageableRequest request, StreamObserver<ArtistsResponse> responseObserver) {
        int page = request.hasPage() ? request.getPage() : 0;
        int size = request.hasSize() ? request.getSize() : 10;
        PageRequest pageRequest = PageRequest.of(page, size);
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
    public void createArtist(CreateArtistRequest request, StreamObserver<ArtistResponse> responseObserver) {
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
    public void updateArtist(UpdateArtistRequest request, StreamObserver<ArtistResponse> responseObserver) {
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

    public static ArtistResponse artistEntityToArtistProtoResponse(ArtistEntity artistEntity) {
        return ArtistResponse.newBuilder()
                .setId(artistEntity.getId() != null ? artistEntity.getId().toString() : "")
                .setName(artistEntity.getName())
                .setBiography(artistEntity.getBiography())
                .setPhoto(artistEntity.getPhoto() != null
                        ? ByteString.copyFrom(artistEntity.getPhoto())
                        : ByteString.EMPTY)
                .build();
    }
}