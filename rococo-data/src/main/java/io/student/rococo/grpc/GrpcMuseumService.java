package io.student.rococo.grpc;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import io.student.rococo.data.entity.CountryEntity;
import io.student.rococo.data.entity.MuseumEntity;
import io.student.rococo.data.repository.CountryRepository;
import io.student.rococo.data.repository.MuseumRepository;
import io.student.rococo.exception.CountryNotFoundException;
import io.student.rococo.exception.FieldValidationException;
import io.student.rococo.exception.MuseumNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.grpc.server.service.GrpcService;

import java.util.UUID;

@GrpcService(interceptors = GlobalGrpcExceptionInterceptor.class)
public class GrpcMuseumService extends MuseumServiceGrpc.MuseumServiceImplBase {
    private final MuseumRepository museumRepository;
    private final CountryRepository countryRepository;

    @Autowired
    public GrpcMuseumService(MuseumRepository museumRepository, CountryRepository countryRepository) {
        this.museumRepository = museumRepository;
        this.countryRepository = countryRepository;
    }

    @Override
    public void allMuseums(PageableRequest request, StreamObserver<MuseumsResponse> responseObserver) {
        int page = request.hasPage() ? request.getPage() : 0;
        int size = request.hasSize() ? request.getSize() : 10;
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<MuseumEntity> result = museumRepository.findAll(pageRequest);

        responseObserver.onNext(MuseumsResponse.newBuilder()
                .addAllMuseums(result.stream().map(GrpcMuseumService::museumEntityToMuseumProtoResponse).toList())
                .setPage(result.getNumber())
                .setSize(result.getSize())
                .setTotalElements(result.getTotalElements())
                .setTotalPages(result.getTotalPages())
                .build());

        responseObserver.onCompleted();
    }

    @Override
    public void findMuseumById(IdRequest request, StreamObserver<MuseumResponse> responseObserver) {
        MuseumEntity museumEntity = museumRepository.findById(UUID.fromString(request.getId()))
                .orElseThrow(() -> new MuseumNotFoundException("Museum not found with id: " + request.getId()));
        responseObserver.onNext(MuseumResponse.newBuilder(museumEntityToMuseumProtoResponse(museumEntity)).build());
        responseObserver.onCompleted();
    }

    @Override
    public void createMuseum(CreateMuseumRequest request, StreamObserver<MuseumResponse> responseObserver) {
        museumRepository.getByTitle(request.getTitle())
                .ifPresent(e -> {
                    throw new FieldValidationException("Museum with exists with title: " + request.getTitle());
                });
        MuseumEntity museumEntity = new MuseumEntity();
        if (request.getTitle().isBlank()) throw new FieldValidationException("Title must not be null");
        museumEntity.setTitle(request.getTitle());
        museumEntity.setDescription(request.getDescription());
        museumEntity.setCity(request.getGeo().getCity());
        if (request.getGeo().getCountry().isBlank()) throw new FieldValidationException("Country Id must not be null");
        CountryEntity countryEntity = countryRepository.findById(UUID.fromString(request.getGeo().getCountry()))
                .orElseThrow(() -> new CountryNotFoundException("Country not found with id: " + request.getGeo().getCountry()));
        museumEntity.setCountry(countryEntity);
        if (!request.getPhoto().isEmpty()) museumEntity.setPhoto(request.getPhoto().toByteArray());
        MuseumEntity result = museumRepository.save(museumEntity);

        responseObserver.onNext(MuseumResponse.newBuilder(museumEntityToMuseumProtoResponse(result)).build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateMuseum(UpdateMuseumRequest request, StreamObserver<MuseumResponse> responseObserver) {
        MuseumEntity museumEntity = museumRepository.findById(UUID.fromString(request.getId()))
                .orElseThrow(() -> new MuseumNotFoundException("Museum not found with id: " + request.getId()));
        if (request.getTitle().isBlank()) throw new FieldValidationException("Title must not be null");
        museumEntity.setTitle(request.getTitle());
        museumEntity.setDescription(request.getDescription());
        museumEntity.setCity(request.getGeo().getCity());
        if (request.getGeo().getCountry().isBlank()) throw new FieldValidationException("Country Id must not be null");
        CountryEntity countryEntity = countryRepository.findById(UUID.fromString(request.getGeo().getCountry()))
                .orElseThrow(() -> new CountryNotFoundException("Country not found with id: " + request.getGeo().getCountry()));
        museumEntity.setCountry(countryEntity);
        if (!request.getPhoto().isEmpty()) museumEntity.setPhoto(request.getPhoto().toByteArray());
        MuseumEntity result = museumRepository.save(museumEntity);

        responseObserver.onNext(MuseumResponse.newBuilder(museumEntityToMuseumProtoResponse(result)).build());
        responseObserver.onCompleted();
    }

    public static MuseumResponse museumEntityToMuseumProtoResponse(MuseumEntity museumEntity) {
        Geo geo = Geo.newBuilder()
                .setCity(museumEntity.getCity())
                .setCountry(museumEntity.getCountry().getId().toString())
                .build();
        return MuseumResponse.newBuilder()
                .setId(museumEntity.getId().toString())
                .setTitle(museumEntity.getTitle())
                .setDescription(museumEntity.getDescription())
                .setPhoto(null == museumEntity.getPhoto()
                        ? ByteString.EMPTY
                        : ByteString.copyFrom(museumEntity.getPhoto()))
                .setGeo(geo)
                .build();
    }
}
