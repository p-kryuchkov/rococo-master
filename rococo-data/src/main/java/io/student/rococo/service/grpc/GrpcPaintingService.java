package io.student.rococo.service.grpc;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import io.student.rococo.data.entity.PaintingEntity;
import io.student.rococo.grpc.*;
import io.student.rococo.service.db.PaintingDbService;
import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static io.student.rococo.utils.GrpcUtils.grpcPageableRequestToSpringPageRequest;

@GrpcService(interceptors = GlobalGrpcExceptionInterceptor.class)
public class GrpcPaintingService extends PaintingServiceGrpc.PaintingServiceImplBase {

    private final PaintingDbService paintingDbService;

    @Autowired
    public GrpcPaintingService(PaintingDbService paintingDbService) {
        this.paintingDbService = paintingDbService;
    }

    @Override
    public void findPaintingsByName(@Nonnull PaintingTitleRequest request, @Nonnull StreamObserver<PaintingsResponse> responseObserver) {
        PageRequest pageRequest = grpcPageableRequestToSpringPageRequest(request.getPageable());
        Page<PaintingEntity> result = paintingDbService.getByTitle(request.getTitle(), pageRequest);

        responseObserver.onNext(PaintingsResponse.newBuilder()
                .addAllPaintings(result.stream().map(GrpcPaintingService::paintingEntityToPaintingProtoResponse).toList())
                .setPage(result.getNumber())
                .setSize(result.getSize())
                .setTotalElements(result.getTotalElements())
                .setTotalPages(result.getTotalPages())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void allPaintings(@Nonnull PageableRequest request, @Nonnull StreamObserver<PaintingsResponse> responseObserver) {
        PageRequest pageRequest = grpcPageableRequestToSpringPageRequest(request);
        Page<PaintingEntity> result = paintingDbService.getAll(pageRequest);
        responseObserver.onNext(PaintingsResponse.newBuilder()
                .addAllPaintings(result.stream().map(GrpcPaintingService::paintingEntityToPaintingProtoResponse).toList())
                .setPage(result.getNumber())
                .setSize(result.getSize())
                .setTotalElements(result.getTotalElements())
                .setTotalPages(result.getTotalPages())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void findPaintingById(@Nonnull IdRequest request, @Nonnull StreamObserver<PaintingResponse> responseObserver) {
        PaintingEntity entity = paintingDbService.getById(request.getId());
        responseObserver.onNext(PaintingResponse.newBuilder(paintingEntityToPaintingProtoResponse(entity)).build());
        responseObserver.onCompleted();
    }

    @Override
    public void findPaintingByArtist(@Nonnull PaintingsByArtistRequest request, @Nonnull StreamObserver<PaintingsResponse> responseObserver) {
        PageRequest pageRequest = grpcPageableRequestToSpringPageRequest(request.getPageable());

        Page<PaintingEntity> result = paintingDbService.getByArtistId(
                request.getArtistId(),
                pageRequest
        );

        responseObserver.onNext(PaintingsResponse.newBuilder()
                .addAllPaintings(result.stream().map(GrpcPaintingService::paintingEntityToPaintingProtoResponse).toList())
                .setPage(result.getNumber())
                .setSize(result.getSize())
                .setTotalElements(result.getTotalElements())
                .setTotalPages(result.getTotalPages())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void createPainting(@Nonnull CreatePaintingRequest request, @Nonnull StreamObserver<PaintingResponse> responseObserver) {
        byte[] content = request.getContent().isEmpty() ? null : request.getContent().toByteArray();
        String artistId = request.hasArtist() ? request.getArtist().getId() : null;
        String museumId = null;
        if (request.hasMuseum() && request.getMuseum().hasId()) {
            museumId = request.getMuseum().getId();
        }
        PaintingEntity created = paintingDbService.create(
                request.getTitle(),
                request.getDescription(),
                content,
                artistId,
                museumId
        );

        responseObserver.onNext(PaintingResponse.newBuilder(paintingEntityToPaintingProtoResponse(created)).build());
        responseObserver.onCompleted();
    }

    @Override
    public void updatePainting(@Nonnull UpdatePaintingRequest request, @Nonnull StreamObserver<PaintingResponse> responseObserver) {
        String title = request.hasTitle() ? request.getTitle() : null;
        String description = request.hasDescription() ? request.getDescription() : null;
        byte[] content = request.hasContent() ? request.getContent().toByteArray() : null;
        String artistId = request.hasArtist() ? request.getArtist().getId() : null;
        String museumId = request.hasMuseum() ? request.getMuseum().getId() : null;

        PaintingEntity updated = paintingDbService.update(
                request.getId(),
                title,
                description,
                content,
                artistId,
                museumId
        );

        responseObserver.onNext(PaintingResponse.newBuilder(paintingEntityToPaintingProtoResponse(updated)).build());
        responseObserver.onCompleted();
    }

    @Nonnull
    private static PaintingResponse paintingEntityToPaintingProtoResponse(@Nonnull PaintingEntity paintingEntity) {
        MuseumResponse museumResponse = paintingEntity.getMuseum() == null
                ? MuseumResponse.getDefaultInstance()
                : MuseumResponse.newBuilder(GrpcMuseumService.museumEntityToMuseumProtoResponse(paintingEntity.getMuseum()))
                .build();

        ArtistResponse artistResponse = paintingEntity.getArtist() == null
                ? ArtistResponse.getDefaultInstance()
                : ArtistResponse.newBuilder(GrpcArtistService.artistEntityToArtistProtoResponse(paintingEntity.getArtist()))
                .build();

        return PaintingResponse.newBuilder()
                .setId(paintingEntity.getId() == null ? "" : paintingEntity.getId().toString())
                .setTitle(paintingEntity.getTitle() == null ? "" : paintingEntity.getTitle())
                .setDescription(paintingEntity.getDescription() == null ? "" : paintingEntity.getDescription())
                .setContent(paintingEntity.getContent() == null
                        ? ByteString.EMPTY
                        : ByteString.copyFrom(paintingEntity.getContent()))
                .setArtist(artistResponse)
                .setMuseum(museumResponse)
                .build();
    }
}