package io.student.rococo.test.rest;

import io.student.rococo.jupiter.annotation.ApiLogin;
import io.student.rococo.jupiter.annotation.Token;
import io.student.rococo.jupiter.annotation.User;
import io.student.rococo.jupiter.annotation.meta.RestTest;
import io.student.rococo.model.CountryJson;
import io.student.rococo.model.page.RestResponsePage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import static org.junit.jupiter.api.Assertions.*;

@RestTest
public class CountryApiTest extends BaseGatewayApiTest {

    @Test
    @User
    @ApiLogin
    @DisplayName("Should Return Country Page With Authorization")
    void shouldReturnCountriesPageWithAuthorization(@Token String token) throws Exception {
        final RestResponsePage<CountryJson> result = gatewayApiClient.getAllCountries(
                bearer(token),
                defaultPageable()
        );

        assertTrue(result.hasContent());
        assertFalse(result.getContent().isEmpty());

        result.getContent().forEach(country -> {
            assertNotNull(country.id());
            assertNotNull(country.name());
            assertFalse(country.name().isBlank());
        });
    }

    @Test
    @DisplayName("Should Not Return Country Page Without Authorization")
    void shouldNotReturnCountriesPageWithoutAuthorization() throws Exception {
        Response<RestResponsePage<CountryJson>> response = gatewayApiClient.getAllCountriesRaw(
                null,
                defaultPageable()
        );

        assertEquals(401, response.code());
        assertNull(response.body());
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("Should Return Country Pageable Correct")
    void shouldReturnCountriesPageable(@Token String token) throws Exception {
        int page = 1;
        int size = 4;

        final RestResponsePage<CountryJson> result = gatewayApiClient.getAllCountries(
                bearer(token),
                pageable(page, size)
        );

        assertEquals(size, result.getSize());
        assertEquals(page, result.getNumber());
        assertTrue(result.getTotalElements() >= result.getContent().size());
        assertFalse(result.getContent().isEmpty());

        result.getContent().forEach(country -> {
            assertNotNull(country.id());
            assertNotNull(country.name());
            assertFalse(country.name().isBlank());
        });
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("Should Return Country Default Pageable Correct")
    void shouldReturnCountriesPageableDefault(@Token String token) throws Exception {
        int page = 0;
        int size = 10;

        final RestResponsePage<CountryJson> result = gatewayApiClient.getAllCountries(
                bearer(token),
                null
        );

        assertEquals(size, result.getSize());
        assertEquals(page, result.getNumber());
        assertTrue(result.getTotalElements() >= result.getContent().size());
        assertFalse(result.getContent().isEmpty());

        result.getContent().forEach(country -> {
            assertNotNull(country.id());
            assertNotNull(country.name());
            assertFalse(country.name().isBlank());
        });
    }
}