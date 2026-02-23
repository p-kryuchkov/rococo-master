package io.student.rococo.service.grpc;

import io.grpc.StatusRuntimeException;
import io.student.rococo.exception.GrpcStatusException;
import io.student.rococo.grpc.CountriesResponse;
import io.student.rococo.grpc.CountriesServiceGrpc;
import io.student.rococo.grpc.PageableRequest;
import io.student.rococo.model.CountryJson;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.student.rococo.utils.GrpcUtils.springPageableToGrpcPageableRequest;

@Component
public class GrpcCountryClient {

    @GrpcClient("grpcDataClient")
    private CountriesServiceGrpc.CountriesServiceBlockingStub stub;

    public Page<CountryJson> getAllCountries(Pageable pageable) {
        try {
            PageableRequest request = springPageableToGrpcPageableRequest(pageable);
            CountriesResponse response = stub.allCountries(request);
            List<CountryJson> items = response.getCountriesList().stream()
                    .map(CountryJson::fromGrpcMessage)
                    .toList();
            return new PageImpl<>(items, pageable, response.getTotalElements());
        } catch (StatusRuntimeException e) {
            throw new GrpcStatusException(e);
        }
    }
}