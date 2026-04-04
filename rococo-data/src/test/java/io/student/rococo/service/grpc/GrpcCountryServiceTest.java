package io.student.rococo.service.grpc;

import io.grpc.stub.StreamObserver;
import io.student.rococo.data.entity.CountryEntity;
import io.student.rococo.grpc.CountriesResponse;
import io.student.rococo.grpc.PageableRequest;
import io.student.rococo.service.db.CountryDbService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrpcCountryServiceTest {

    @Mock
    private CountryDbService countryDbService;

    @Mock
    private StreamObserver<CountriesResponse> countriesResponseObserver;

    @InjectMocks
    private GrpcCountryService grpcCountryService;

    @Captor
    private ArgumentCaptor<CountriesResponse> countriesResponseCaptor;

    @Test
    void allCountries() {
        final UUID firstId = UUID.randomUUID();
        final UUID secondId = UUID.randomUUID();

        final String firstName = "Italy";
        final String secondName = "France";

        final int page = 1;
        final int size = 2;
        final long totalElements = 5L;
        final int totalPages = 3;

        final CountryEntity firstCountry = createCountryEntity(firstId, firstName);
        final CountryEntity secondCountry = createCountryEntity(secondId, secondName);

        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<CountryEntity> countriesPage = new PageImpl<>(
                List.of(firstCountry, secondCountry),
                pageRequest,
                totalElements
        );

        when(countryDbService.getAll(pageRequest)).thenReturn(countriesPage);

        final PageableRequest request = PageableRequest.newBuilder()
                .setPage(page)
                .setSize(size)
                .build();

        grpcCountryService.allCountries(request, countriesResponseObserver);

        verify(countryDbService).getAll(pageRequest);
        verify(countriesResponseObserver).onNext(countriesResponseCaptor.capture());
        verify(countriesResponseObserver).onCompleted();

        final CountriesResponse response = countriesResponseCaptor.getValue();

        assertEquals(2, response.getCountriesCount());

        assertEquals(firstId.toString(), response.getCountries(0).getId());
        assertEquals(firstName, response.getCountries(0).getName());

        assertEquals(secondId.toString(), response.getCountries(1).getId());
        assertEquals(secondName, response.getCountries(1).getName());

        assertEquals(page, response.getPage());
        assertEquals(size, response.getSize());
        assertEquals(totalElements, response.getTotalElements());
        assertEquals(totalPages, response.getTotalPages());
    }

    @Test
    void allCountriesWithDefaultSize() {
        final UUID id = UUID.randomUUID();
        final String name = "Italy";

        final int page = 0;
        final int size = 20;
        final long totalElements = 1L;
        final int totalPages = 1;

        final CountryEntity country = createCountryEntity(id, name);

        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<CountryEntity> countriesPage = new PageImpl<>(
                List.of(country),
                pageRequest,
                totalElements
        );

        when(countryDbService.getAll(pageRequest)).thenReturn(countriesPage);

        final PageableRequest request = PageableRequest.newBuilder()
                .setPage(page)
                .build();

        grpcCountryService.allCountries(request, countriesResponseObserver);

        verify(countryDbService).getAll(pageRequest);
        verify(countriesResponseObserver).onNext(countriesResponseCaptor.capture());
        verify(countriesResponseObserver).onCompleted();

        final CountriesResponse response = countriesResponseCaptor.getValue();

        assertEquals(1, response.getCountriesCount());
        assertEquals(id.toString(), response.getCountries(0).getId());
        assertEquals(name, response.getCountries(0).getName());
        assertEquals(page, response.getPage());
        assertEquals(size, response.getSize());
        assertEquals(totalElements, response.getTotalElements());
        assertEquals(totalPages, response.getTotalPages());
    }

    private CountryEntity createCountryEntity(final UUID id, final String name) {
        final CountryEntity country = new CountryEntity();
        country.setId(id);
        country.setName(name);
        return country;
    }
}