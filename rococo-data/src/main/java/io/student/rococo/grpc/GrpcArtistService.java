package io.student.rococo.grpc;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import io.student.rococo.data.entity.ArtistEntity;
import io.student.rococo.data.repository.ArtistRepository;
import io.student.rococo.exception.ArtistNotFoundException;
import io.student.rococo.exception.FieldValidationException;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.grpc.server.service.GrpcService;

import java.util.UUID;


@GrpcService(interceptors = GlobalGrpcExceptionInterceptor.class)
public class GrpcArtistService extends ArtistServiceGrpc.ArtistServiceImplBase {
    private final ArtistRepository artistRepository;

    @Autowired
    public GrpcArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    public void allArtists(PageableRequest request, StreamObserver<ArtistsResponse> responseObserver) {
        int page = request.hasPage() ? request.getPage() : 0;
        int size = request.hasSize() ? request.getSize() : 10;
        PageRequest pageRequest = PageRequest.of(page, size);
        if (request.hasPage() && request.hasSize()) pageRequest = PageRequest.of(request.getPage(), request.getSize());
        Page<ArtistEntity> result = artistRepository.findAll(pageRequest);

        responseObserver.onNext(ArtistsResponse.newBuilder()
                .addAllArtists(result.stream().map(GrpcArtistService::artistEntityToArtistProtoResponse).toList())
                .setPage(result.getNumber())
                .setSize(result.getSize())
                .setTotalElements(result.getTotalElements())
                .setTotalPages(result.getTotalPages())
                .build());

        responseObserver.onCompleted();
    }

    @Override
    public void createArtist(CreateArtistRequest request, StreamObserver<ArtistResponse> responseObserver) {
        artistRepository.getByName(request.getName())
                .ifPresent(e->{
                    throw new EntityExistsException("Artist with exists with name: " + request.getName());
                });
        ArtistEntity artistEntity = new ArtistEntity();
        if (request.getName().isBlank()) throw new FieldValidationException("Name must not be null");
        artistEntity.setName(request.getName());
        if (request.getBiography().isBlank()) throw new FieldValidationException("Biography must not be null");
        artistEntity.setBiography(request.getBiography());
        if (!request.getPhoto().isEmpty()) artistEntity.setPhoto(request.getPhoto().toByteArray());
        ArtistEntity result = artistRepository.save(artistEntity);

        responseObserver.onNext(ArtistResponse.newBuilder(artistEntityToArtistProtoResponse(result)).build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateArtist(UpdateArtistRequest request, StreamObserver<ArtistResponse> responseObserver) {
        ArtistEntity artistEntity = artistRepository.findById(UUID.fromString(request.getId()))
                .orElseThrow(() -> new ArtistNotFoundException("Artist not found with id: " + request.getId()));
        artistEntity.setName(request.getName());
        artistEntity.setBiography(request.getBiography());
        if (!request.getPhoto().isEmpty()) artistEntity.setPhoto(request.getPhoto().toByteArray());
        ArtistEntity result = artistRepository.save(artistEntity);

        responseObserver.onNext(ArtistResponse.newBuilder(artistEntityToArtistProtoResponse(result)).build());
        responseObserver.onCompleted();
    }

    public static ArtistResponse artistEntityToArtistProtoResponse(ArtistEntity artistEntity) {
        return ArtistResponse.newBuilder()
                .setId(null == artistEntity.getId()
                        ? ""
                        : artistEntity.getId().toString())
                .setName(artistEntity.getName())
                .setBiography(artistEntity.getBiography())
                .setPhoto(null == artistEntity.getPhoto()
                        ? ByteString.EMPTY
                        : ByteString.copyFrom(artistEntity.getPhoto()))
                .build();
    }
}
