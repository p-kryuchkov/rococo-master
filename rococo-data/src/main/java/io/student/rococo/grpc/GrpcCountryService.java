package io.student.rococo.grpc;

import io.grpc.stub.StreamObserver;

import io.student.rococo.data.entity.CountryEntity;
import io.student.rococo.data.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.grpc.server.service.GrpcService;


@GrpcService(interceptors = GlobalGrpcExceptionInterceptor.class)
public class GrpcCountryService extends CountriesServiceGrpc.CountriesServiceImplBase {
    private final CountryRepository countryRepository;

    @Autowired
    public GrpcCountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public void allCountriesRequest(PageableRequest request, StreamObserver<CountriesResponse> responseObserver) {
        int page = request.hasPage() ? request.getPage() : 0;
        int size = request.hasSize() ? request.getSize() : 20;
        PageRequest pageRequest = PageRequest.of(page, size);
        if (request.hasPage() && request.hasSize()) pageRequest = PageRequest.of(request.getPage(), request.getSize());
        Page<CountryEntity> result = countryRepository.findAll(pageRequest);
        responseObserver.onNext(CountriesResponse.newBuilder()
                .addAllCountries(result.stream().map(
                        countryEntity -> {
                            return CountryResponse.newBuilder()
                                    .setId(countryEntity.getId().toString())
                                    .setName(countryEntity.getName())
                                    .build();
                        }).toList())
                .setPage(result.getNumber())
                .setSize(result.getSize())
                .setTotalElements(result.getTotalElements())
                .setTotalPages(result.getTotalPages())
                .build());

        responseObserver.onCompleted();
    }
}
