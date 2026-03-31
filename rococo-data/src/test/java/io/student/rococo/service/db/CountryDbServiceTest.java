package io.student.rococo.service.db;

import io.student.rococo.data.entity.CountryEntity;
import io.student.rococo.data.repository.CountryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountryDbServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryDbService countryDbService;

    @Test
    void shouldReturnAllCountries() {
        final Pageable pageable = PageRequest.of(0, 10);

        final CountryEntity firstCountry = new CountryEntity();
        firstCountry.setName("Italy");

        final CountryEntity secondCountry = new CountryEntity();
        secondCountry.setName("France");

        final Page<CountryEntity> page = new PageImpl<>(List.of(firstCountry, secondCountry));

        when(countryRepository.findAll(pageable)).thenReturn(page);

        Page<CountryEntity> result = countryDbService.getAll(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("Italy", result.getContent().get(0).getName());
        assertEquals("France", result.getContent().get(1).getName());

        verify(countryRepository).findAll(pageable);
    }
}