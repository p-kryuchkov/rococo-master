package io.student.rococo.grpc;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import io.student.rococo.data.entity.ArtistEntity;
import io.student.rococo.data.entity.MuseumEntity;
import io.student.rococo.data.entity.PaintingEntity;
import io.student.rococo.data.repository.ArtistRepository;
import io.student.rococo.data.repository.MuseumRepository;
import io.student.rococo.data.repository.PaintingRepository;
import io.student.rococo.exception.ArtistNotFoundException;
import io.student.rococo.exception.FieldValidationException;
import io.student.rococo.exception.MuseumNotFoundException;
import io.student.rococo.exception.PaintingNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.grpc.server.service.GrpcService;

import java.util.UUID;

@GrpcService(interceptors = GlobalGrpcExceptionInterceptor.class)
public class GrpcPaintingService extends PaintingServiceGrpc.PaintingServiceImplBase {
    private final PaintingRepository paintingRepository;
    private final MuseumRepository museumRepository;
    private final ArtistRepository artistRepository;

    @Autowired
    public GrpcPaintingService(PaintingRepository paintingRepository, MuseumRepository museumRepository, ArtistRepository artistRepository) {
        this.paintingRepository = paintingRepository;
        this.museumRepository = museumRepository;
        this.artistRepository = artistRepository;
    }

    @Override
    public void allPaintings(PageableRequest request, StreamObserver<PaintingsResponse> responseObserver) {
        int page = request.hasPage() ? request.getPage() : 0;
        int size = request.hasSize() ? request.getSize() : 10;
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<PaintingEntity> result = paintingRepository.findAll(pageRequest);
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
    public void findPaintingById(IdRequest request, StreamObserver<PaintingResponse> responseObserver) {
        PaintingEntity paintingEntity = paintingRepository.findById(UUID.fromString(request.getId()))
                .orElseThrow(() -> new PaintingNotFoundException("Painting not found with id: " + request.getId()));
        responseObserver.onNext(PaintingResponse.newBuilder(paintingEntityToPaintingProtoResponse(paintingEntity)).build());
        responseObserver.onCompleted();
    }

    @Override
    public void findPaintingByArtist(PaintingsByArtistRequest request, StreamObserver<PaintingsResponse> responseObserver) {
        int page = request.hasPageable() && request.getPageable().hasPage() ? request.getPageable().getPage() : 0;
        int size = request.hasPageable() && request.getPageable().hasSize() ? request.getPageable().getSize() : 10;
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<PaintingEntity> result = paintingRepository.findByArtist_Id(UUID.fromString(request.getArtistId()), pageRequest);
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
    public void createPainting(CreatePaintingRequest request, StreamObserver<PaintingResponse> responseObserver) {
        PaintingEntity paintingEntity = new PaintingEntity();
        if (request.getTitle().isBlank()) throw new FieldValidationException("Title must not be null");
        paintingEntity.setTitle(request.getTitle());
        paintingEntity.setDescription(request.getDescription());
        if (!request.getContent().isEmpty()) paintingEntity.setContent(request.getContent().toByteArray());
        if (!request.hasArtist()
                || !request.getArtist().hasId()
                || request.getArtist().getId().isBlank())
            throw new FieldValidationException("Artist Id must not be null");
        ArtistEntity artistEntity = artistRepository.findById(UUID.fromString(request.getArtist().getId()))
                .orElseThrow(() -> new ArtistNotFoundException("Artist not found by id: " + request.getArtist().getId()));
        paintingEntity.setArtist(artistEntity);
        if (request.hasMuseum()
                && request.getMuseum().hasId()
                && !request.getMuseum().getId().isBlank()) {
            MuseumEntity museumEntity = museumRepository.findById(UUID.fromString(request.getMuseum().getId()))
                    .orElseThrow(() -> new MuseumNotFoundException("Museum not found by id: " + request.getMuseum().getId()));
            paintingEntity.setMuseum(museumEntity);
        }
        PaintingEntity result = paintingRepository.save(paintingEntity);

        responseObserver.onNext(PaintingResponse.newBuilder(paintingEntityToPaintingProtoResponse(result)).build());
        responseObserver.onCompleted();
    }

    @Override
    public void updatePainting(UpdatePaintingRequest request, StreamObserver<PaintingResponse> responseObserver) {
        PaintingEntity paintingEntity = paintingRepository.findById(UUID.fromString(request.getId()))
                .orElseThrow(() -> new PaintingNotFoundException("Painting not found with id: " + request.getId()));
        if (request.getTitle().isBlank()) throw new FieldValidationException("Title must not be null");
        paintingEntity.setTitle(request.getTitle());
        paintingEntity.setDescription(request.getDescription());
        if (!request.getContent().isEmpty()) paintingEntity.setContent(request.getContent().toByteArray());
        if (!request.hasArtist()
                || !request.getArtist().hasId()
                || request.getArtist().getId().isBlank())
            throw new FieldValidationException("Artist Id must not be null");
        ArtistEntity artistEntity = artistRepository.findById(UUID.fromString(request.getArtist().getId()))
                .orElseThrow(() -> new ArtistNotFoundException("Artist not found by id: " + request.getArtist().getId()));
        paintingEntity.setArtist(artistEntity);
        if (request.hasMuseum()
                && request.getMuseum().hasId()
                && !request.getMuseum().getId().isBlank()) {
            MuseumEntity museumEntity = museumRepository.findById(UUID.fromString(request.getMuseum().getId()))
                    .orElseThrow(() -> new MuseumNotFoundException("Museum not found by id: " + request.getMuseum().getId()));
            paintingEntity.setMuseum(museumEntity);
        }
        PaintingEntity result = paintingRepository.save(paintingEntity);

        responseObserver.onNext(PaintingResponse.newBuilder(paintingEntityToPaintingProtoResponse(result)).build());
        responseObserver.onCompleted();
    }

    public static PaintingResponse paintingEntityToPaintingProtoResponse(PaintingEntity paintingEntity) {
        ArtistResponse artistResponse = ArtistResponse
                .newBuilder(
                        GrpcArtistService.artistEntityToArtistProtoResponse(
                                paintingEntity.getArtist()
                        )
                ).build();
        MuseumResponse museumResponse = MuseumResponse
                .newBuilder(
                        GrpcMuseumService.museumEntityToMuseumProtoResponse(
                                paintingEntity.getMuseum()
                        )
                ).build();

        return PaintingResponse.newBuilder()
                .setId(paintingEntity.getId().toString())
                .setTitle(paintingEntity.getTitle())
                .setDescription(paintingEntity.getDescription())
                .setContent(null == paintingEntity.getContent()
                        ? ByteString.EMPTY
                        : ByteString.copyFrom(paintingEntity.getContent()))
                .setArtist(artistResponse)
                .setMuseum(museumResponse)
                .build();
    }
}
