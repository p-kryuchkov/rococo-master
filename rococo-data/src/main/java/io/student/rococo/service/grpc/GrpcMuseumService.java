package io.student.rococo.service.grpc;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import io.student.rococo.data.entity.MuseumEntity;
import io.student.rococo.grpc.*;
import io.student.rococo.service.db.MuseumDbService;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static io.student.rococo.utils.GrpcUtils.grpcPageableRequestToSpringPageRequest;

@GrpcService(interceptors = GlobalGrpcExceptionInterceptor.class)
public class GrpcMuseumService extends MuseumServiceGrpc.MuseumServiceImplBase {

    private final MuseumDbService museumDbService;

    @Autowired
    public GrpcMuseumService(MuseumDbService museumDbService) {
        this.museumDbService = museumDbService;
    }

    @Override
    public void allMuseums(PageableRequest request, StreamObserver<MuseumsResponse> responseObserver) {
        PageRequest pageRequest = grpcPageableRequestToSpringPageRequest(request);
        Page<MuseumEntity> result = museumDbService.getAll(pageRequest);

        responseObserver.onNext(MuseumsResponse.newBuilder()
                .addAllMuseums(result.stream()
                        .map(GrpcMuseumService::museumEntityToMuseumProtoResponse)
                        .toList())
                .setPage(result.getNumber())
                .setSize(result.getSize())
                .setTotalElements(result.getTotalElements())
                .setTotalPages(result.getTotalPages())
                .build());

        responseObserver.onCompleted();
    }

    @Override
    public void findMuseumsByName(MuseumTitleRequest request, StreamObserver<MuseumsResponse> responseObserver) {
        PageRequest pageRequest = grpcPageableRequestToSpringPageRequest(request.getPageable());
        Page<MuseumEntity> result = museumDbService.getByTitle(request.getTitle(), pageRequest);

        responseObserver.onNext(MuseumsResponse.newBuilder()
                .addAllMuseums(result.stream()
                        .map(GrpcMuseumService::museumEntityToMuseumProtoResponse)
                        .toList())
                .setPage(result.getNumber())
                .setSize(result.getSize())
                .setTotalElements(result.getTotalElements())
                .setTotalPages(result.getTotalPages())
                .build());

        responseObserver.onCompleted();
    }

    @Override
    public void findMuseumById(IdRequest request, StreamObserver<MuseumResponse> responseObserver) {
        MuseumEntity entity = museumDbService.getById(request.getId());
        responseObserver.onNext(museumEntityToMuseumProtoResponse(entity));
        responseObserver.onCompleted();
    }

    @Override
    public void createMuseum(CreateMuseumRequest request, StreamObserver<MuseumResponse> responseObserver) {
        byte[] photo = request.getPhoto().isEmpty() ? null : request.getPhoto().toByteArray();

        MuseumEntity created = museumDbService.create(
                request.getTitle(),
                request.getDescription(),
                request.getGeo().getCity(),
                request.getGeo().getCountryId(),
                photo
        );

        responseObserver.onNext(museumEntityToMuseumProtoResponse(created));
        responseObserver.onCompleted();
    }

    @Override
    public void updateMuseum(UpdateMuseumRequest request, StreamObserver<MuseumResponse> responseObserver) {
        var geo = request.hasGeo() ? request.getGeo() : null;

        MuseumEntity updated = museumDbService.update(
                request.getId(),
                request.hasTitle() ? request.getTitle() : null,
                request.hasDescription() ? request.getDescription() : null,
                geo != null ? geo.getCity() : null,
                geo != null ? geo.getCountryId() : null,
                request.hasPhoto() ? request.getPhoto().toByteArray() : null
        );

        responseObserver.onNext(museumEntityToMuseumProtoResponse(updated));
        responseObserver.onCompleted();
    }

    public static MuseumResponse museumEntityToMuseumProtoResponse(MuseumEntity museumEntity) {
        Geo geo = Geo.newBuilder()
                .setCity(museumEntity.getCity() == null ? "" : museumEntity.getCity())
                .setCountryId(museumEntity.getCountry() == null ? "" : museumEntity.getCountry().getId().toString())
                .setCountryName(museumEntity.getCountry() == null ? "" : museumEntity.getCountry().getName())
                .build();

        return MuseumResponse.newBuilder()
                .setId(museumEntity.getId() == null
                        ? ""
                        : museumEntity.getId().toString())
                .setTitle(museumEntity.getTitle() == null
                        ? ""
                        : museumEntity.getTitle())
                .setDescription(museumEntity.getDescription() == null
                        ? ""
                        : museumEntity.getDescription())
                .setPhoto(museumEntity.getPhoto() == null
                        ? ByteString.EMPTY
                        : ByteString.copyFrom(museumEntity.getPhoto()))
                .setGeo(geo)
                .build();
    }
}