package io.student.rococo.service.grpc;

import io.grpc.stub.StreamObserver;

import io.student.rococo.data.entity.CountryEntity;
import io.student.rococo.data.repository.CountryRepository;
import io.student.rococo.grpc.CountriesResponse;
import io.student.rococo.grpc.CountriesServiceGrpc;
import io.student.rococo.grpc.CountryResponse;
import io.student.rococo.grpc.PageableRequest;
import io.student.rococo.service.db.CountryDbService;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


@GrpcService(interceptors = GlobalGrpcExceptionInterceptor.class)
public class GrpcCountryService extends CountriesServiceGrpc.CountriesServiceImplBase {

    private final CountryDbService countryDbService;

    @Autowired
    public GrpcCountryService(CountryDbService countryDbService) {
        this.countryDbService = countryDbService;
    }

    @Override
    public void allCountries(PageableRequest request, StreamObserver<CountriesResponse> responseObserver) {
        int page = request.hasPage() ? request.getPage() : 0;
        int size = request.hasSize() ? request.getSize() : 20;
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<CountryEntity> result = countryDbService.getAll(pageRequest);

        CountriesResponse response = CountriesResponse.newBuilder()
                .addAllCountries(result.stream()
                        .map(countryEntity -> {
                            return CountryResponse.newBuilder()
                                    .setId(countryEntity.getId().toString())
                                    .setName(countryEntity.getName())
                                    .build();})
                        .toList())
                .setPage(result.getNumber())
                .setSize(result.getSize())
                .setTotalElements(result.getTotalElements())
                .setTotalPages(result.getTotalPages())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
