package io.student.rococo.test.rest;

import io.student.rococo.jupiter.annotation.ApiLogin;
import io.student.rococo.jupiter.annotation.Museum;
import io.student.rococo.jupiter.annotation.Token;
import io.student.rococo.jupiter.annotation.meta.RestTest;
import io.student.rococo.model.GeoJson;
import io.student.rococo.model.MuseumJson;
import io.student.rococo.model.page.RestResponsePage;
import io.student.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import static org.junit.jupiter.api.Assertions.*;

@RestTest
public class MuseumApiTest extends BaseGatewayApiTest {

    @Test
    @ApiLogin(username = "TestDefaultUser")
    @DisplayName("Should Return Museum Page With Authorization")
    void shouldReturnMuseumsPageWithAuthorization(@Token String token) throws Exception {
        final RestResponsePage<MuseumJson> result = gatewayApiClient.getAllMuseums(
                bearer(token),
                defaultPageable()
        );
        assertTrue(result.hasContent());
        assertFalse(result.getContent().isEmpty());
    }

    @Test
    @DisplayName("Should Return Museum Page Without Authorization")
    void shouldReturnMuseumsPageWithoutAuthorization() throws Exception {
        final RestResponsePage<MuseumJson> result = gatewayApiClient.getAllMuseums(
                null,
                defaultPageable()
        );
        assertTrue(result.hasContent());
        assertFalse(result.getContent().isEmpty());
    }

    @Test
    @DisplayName("Should Return Museum Pageable Correct")
    void shouldReturnMuseumsPageable() throws Exception {
        int page = 1;
        int size = 4;
        final RestResponsePage<MuseumJson> result = gatewayApiClient.getAllMuseums(
                null,
                pageable(page, size)
        );
        assertEquals(size, result.getSize());
        assertEquals(page, result.getNumber());
        assertTrue(result.getTotalElements() >= result.getSize());
        assertFalse(result.getContent().isEmpty());
    }

    @Test
    @DisplayName("Should Return Museum Default Pageable")
    void shouldReturnMuseumsDefaultPageable() throws Exception {
        int page = 0;
        int size = 10;
        final RestResponsePage<MuseumJson> result = gatewayApiClient.getAllMuseums(
                null,
                null
        );
        assertEquals(size, result.getSize());
        assertEquals(page, result.getNumber());
        assertTrue(result.getTotalElements() >= result.getSize());
        assertFalse(result.getContent().isEmpty());
    }

    @Test
    @ApiLogin(username = "TestDefaultUser")
    @Museum
    @DisplayName("Should Return Museum By Id With Authorization")
    void shouldReturnMuseumByIdWithAuthorization(@Token String token,
                                                 @Museum MuseumJson museum) throws Exception {
        final MuseumJson result = gatewayApiClient.getMuseumById(
                bearer(token),
                museum.id().toString()
        );

        assertEquals(museum.id(), result.id());
        assertEquals(museum.description(), result.description());
        assertEquals(museum.title(), result.title());
        assertEquals(museum.photo(), result.photo());
        assertEquals(museum.geo(), result.geo());
    }

    @Test
    @Museum
    @DisplayName("Should Return Museum By Id Without Authorization")
    void shouldReturnMuseumByIdWithoutAuthorization(@Museum MuseumJson museum) throws Exception {
        final MuseumJson result = gatewayApiClient.getMuseumById(
                null,
                museum.id().toString()
        );

        assertEquals(museum.id(), result.id());
        assertEquals(museum.description(), result.description());
        assertEquals(museum.title(), result.title());
        assertEquals(museum.photo(), result.photo());
        assertEquals(museum.geo(), result.geo());
    }

    @Test
    @ApiLogin(username = "TestDefaultUser")
    @Museum
    @DisplayName("Should Update Museum Title With Authorization")
    void shouldUpdateMuseumTitleWithAuthorization(@Token String token,
                                                  @Museum MuseumJson museum) throws Exception {
        String updatedTitle = RandomDataUtils.randomAirport()+ " " + RandomDataUtils.randomAirport();

        final MuseumJson updateRequest = new MuseumJson(
                museum.id(),
                null,
                updatedTitle,
                null,
                null
        );

        final MuseumJson updatedMuseum = gatewayApiClient.updateMuseum(
                bearer(token),
                updateRequest
        );

        assertEquals(museum.id(), updatedMuseum.id());
        assertEquals(museum.description(), updatedMuseum.description());
        assertEquals(updatedTitle, updatedMuseum.title());
        assertEquals(museum.photo(), updatedMuseum.photo());
        assertEquals(museum.geo(), updatedMuseum.geo());

        final MuseumJson result = gatewayApiClient.getMuseumById(
                bearer(token),
                museum.id().toString()
        );

        assertEquals(museum.id(), result.id());
        assertEquals(museum.description(), result.description());
        assertEquals(updatedTitle, result.title());
        assertEquals(museum.photo(), result.photo());
        assertEquals(museum.geo(), result.geo());
    }

    @Test
    @ApiLogin(username = "TestDefaultUser")
    @Museum
    @DisplayName("Should Update Museum Description With Authorization")
    void shouldUpdateMuseumDescriptionWithAuthorization(@Token String token,
                                                        @Museum MuseumJson museum) throws Exception {
        String updatedDescription = RandomDataUtils.randomSentence(10);

        final MuseumJson updateRequest = new MuseumJson(
                museum.id(),
                updatedDescription,
                null,
                null,
                null
        );

        final MuseumJson updatedMuseum = gatewayApiClient.updateMuseum(
                bearer(token),
                updateRequest
        );

        assertEquals(museum.id(), updatedMuseum.id());
        assertEquals(updatedDescription, updatedMuseum.description());
        assertEquals(museum.title(), updatedMuseum.title());
        assertEquals(museum.photo(), updatedMuseum.photo());
        assertEquals(museum.geo(), updatedMuseum.geo());

        final MuseumJson result = gatewayApiClient.getMuseumById(
                bearer(token),
                museum.id().toString()
        );

        assertEquals(museum.id(), result.id());
        assertEquals(updatedDescription, result.description());
        assertEquals(museum.title(), result.title());
        assertEquals(museum.photo(), result.photo());
        assertEquals(museum.geo(), result.geo());
    }

    @Test
    @ApiLogin(username = "TestDefaultUser")
    @Museum
    @DisplayName("Should Update Museum Photo With Authorization")
    void shouldUpdateMuseumPhotoWithAuthorization(@Token String token,
                                                  @Museum MuseumJson museum) throws Exception {
        final MuseumJson updateRequest = new MuseumJson(
                museum.id(),
                null,
                null,
                IMAGE,
                null
        );

        final MuseumJson updatedMuseum = gatewayApiClient.updateMuseum(
                bearer(token),
                updateRequest
        );

        assertEquals(museum.id(), updatedMuseum.id());
        assertEquals(museum.description(), updatedMuseum.description());
        assertEquals(museum.title(), updatedMuseum.title());
        assertEquals(IMAGE, updatedMuseum.photo());
        assertEquals(museum.geo(), updatedMuseum.geo());

        final MuseumJson result = gatewayApiClient.getMuseumById(
                bearer(token),
                museum.id().toString()
        );

        assertEquals(museum.id(), result.id());
        assertEquals(museum.description(), result.description());
        assertEquals(museum.title(), result.title());
        assertEquals(IMAGE, result.photo());
        assertEquals(museum.geo(), result.geo());
    }

    @Test
    @ApiLogin(username = "TestDefaultUser")
    @Museum
    @DisplayName("Should Update Museum Geo With Authorization")
    void shouldUpdateMuseumGeoWithAuthorization(@Token String token,
                                                @Museum MuseumJson museum) throws Exception {
        GeoJson updatedGeo = new GeoJson(
                RandomDataUtils.randomName(),
                museum.geo().country()
        );

        final MuseumJson updateRequest = new MuseumJson(
                museum.id(),
                null,
                null,
                null,
                updatedGeo
        );

        final MuseumJson updatedMuseum = gatewayApiClient.updateMuseum(
                bearer(token),
                updateRequest
        );

        assertEquals(museum.id(), updatedMuseum.id());
        assertEquals(museum.description(), updatedMuseum.description());
        assertEquals(museum.title(), updatedMuseum.title());
        assertEquals(museum.photo(), updatedMuseum.photo());
        assertEquals(updatedGeo, updatedMuseum.geo());

        final MuseumJson result = gatewayApiClient.getMuseumById(
                bearer(token),
                museum.id().toString()
        );

        assertEquals(museum.id(), result.id());
        assertEquals(museum.description(), result.description());
        assertEquals(museum.title(), result.title());
        assertEquals(museum.photo(), result.photo());
        assertEquals(updatedGeo, result.geo());
    }

    @Test
    @Museum
    @DisplayName("Should Not Update Museum Without Authorization")
    void shouldNotUpdateMuseumWithoutAuthorization(@Museum MuseumJson museum) throws Exception {
        String updatedTitle = RandomDataUtils.randomName() + " " + RandomDataUtils.randomSurname();

        MuseumJson updateRequest = new MuseumJson(
                museum.id(),
                null,
                updatedTitle,
                null,
                null
        );

        Response<MuseumJson> response = gatewayApiClient.updateMuseumRaw(null, updateRequest);

        assertEquals(401, response.code());

        MuseumJson actualMuseum = gatewayApiClient.getMuseumById(null, museum.id().toString());

        assertEquals(museum.id(), actualMuseum.id());
        assertEquals(museum.description(), actualMuseum.description());
        assertEquals(museum.title(), actualMuseum.title());
        assertEquals(museum.photo(), actualMuseum.photo());
        assertEquals(museum.geo(), actualMuseum.geo());
    }

    @Test
    @ApiLogin(username = "TestDefaultUser")
    @Museum
    @DisplayName("Should Create Museum With Authorization")
    void shouldCreateMuseumWithAuthorization(@Token String token,
                                             @Museum MuseumJson museum) throws Exception {
        String title = RandomDataUtils.randomAirport()+ " " + RandomDataUtils.randomAirport();;
        String description = RandomDataUtils.randomSentence(10);

        final MuseumJson request = new MuseumJson(
                null,
                description,
                title,
                null,
                museum.geo()
        );

        final MuseumJson result = gatewayApiClient.createMuseum(
                bearer(token),
                request
        );

        assertNotNull(result.id());
        assertEquals(request.description(), result.description());
        assertEquals(request.title(), result.title());
        assertEquals(request.photo(), result.photo());
        assertEquals(request.geo(), result.geo());
    }

    @Test
    @Museum
    @DisplayName("Should Not Create Museum Without Authorization")
    void shouldNotCreateMuseumWithoutAuthorization(@Museum MuseumJson museum) throws Exception {
        String title = RandomDataUtils.randomAirport()+ " " + RandomDataUtils.randomAirport();;
        String description = RandomDataUtils.randomSentence(10);

        MuseumJson request = new MuseumJson(
                null,
                description,
                title,
                null,
                museum.geo()
        );

        long totalBefore = gatewayApiClient.getAllMuseums(null, defaultPageable()).getTotalElements();

        Response<MuseumJson> response = gatewayApiClient.createMuseumRaw(null, request);

        assertEquals(401, response.code());

        RestResponsePage<MuseumJson> result = gatewayApiClient.getAllMuseums(null, defaultPageable());
        assertEquals(totalBefore, result.getTotalElements());
    }
}