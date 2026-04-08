package io.student.rococo.test.rest;

import io.student.rococo.jupiter.annotation.ApiLogin;
import io.student.rococo.jupiter.annotation.Artist;
import io.student.rococo.jupiter.annotation.Token;
import io.student.rococo.jupiter.annotation.meta.RestTest;
import io.student.rococo.model.ArtistJson;
import io.student.rococo.model.page.RestResponsePage;
import io.student.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import static org.junit.jupiter.api.Assertions.*;

@RestTest
public class ArtistApiTest extends BaseGatewayApiTest {

    @Test
    @ApiLogin(username = "TestDefaultUser")
    @DisplayName("Should Return Artist Page With Authorization")
    void shouldReturnArtistsPageWithAuthorization(@Token String token) throws Exception {
        final RestResponsePage<ArtistJson> result = gatewayApiClient.getAllArtists(bearer(token),
                defaultPageable()
        );
        assertTrue(result.hasContent());
        assertFalse(result.getContent().isEmpty());
    }

    @Test
    @DisplayName("Should Return Artist Page Without Authorization")
    void shouldReturnArtistsPageWithoutAuthorization() throws Exception {
        final RestResponsePage<ArtistJson> result = gatewayApiClient.getAllArtists(null,
                defaultPageable()
        );
        assertTrue(result.hasContent());
        assertFalse(result.getContent().isEmpty());
    }

    @Test
    @DisplayName("Should Return Artist Pageable Correct")
    void shouldReturnArtistsPageable() throws Exception {
        int page = 1;
        int size = 4;
        final RestResponsePage<ArtistJson> result = gatewayApiClient.getAllArtists(null,
                pageable(page, size)
        );
        assertEquals(size, result.getSize());
        assertEquals(page, result.getNumber());
        assertTrue(result.getTotalElements() >= result.getSize());
        assertFalse(result.getContent().isEmpty());
    }

    @Test
    @DisplayName("Should Return Artist Default Pageable")
    void shouldReturnArtistsDefaultPageable() throws Exception {
        int page = 0;
        int size = 10;
        final RestResponsePage<ArtistJson> result = gatewayApiClient.getAllArtists(null,
                null
        );
        assertEquals(size, result.getSize());
        assertEquals(page, result.getNumber());
        assertTrue(result.getTotalElements() >= result.getSize());
        assertFalse(result.getContent().isEmpty());
    }

    @Test
    @ApiLogin(username = "TestDefaultUser")
    @Artist
    @DisplayName("Should Return Artist By Id With Authorization")
    void shouldReturnArtistByIdWithAuthorization(@Token String token,
                                                 @Artist ArtistJson artist) throws Exception {
        final ArtistJson result = gatewayApiClient.getArtistById(
                bearer(token),
                artist.id().toString()
        );

        assertEquals(artist.id(), result.id());
        assertEquals(artist.name(), result.name());
        assertEquals(artist.biography(), result.biography());
        assertEquals(artist.photo(), result.photo());
    }

    @Test
    @Artist
    @DisplayName("Should Return Artist By Id Without Authorization")
    void shouldReturnArtistByIdWithoutAuthorization(@Artist ArtistJson artist) throws Exception {
        final ArtistJson result = gatewayApiClient.getArtistById(
                null,
                artist.id().toString()
        );

        assertEquals(artist.id(), result.id());
        assertEquals(artist.name(), result.name());
        assertEquals(artist.biography(), result.biography());
        assertEquals(artist.photo(), result.photo());
    }

    @Test
    @ApiLogin(username = "TestDefaultUser")
    @Artist
    @DisplayName("Should Update Artist Name With Authorization")
    void shouldUpdateArtistNameWithAuthorization(@Token String token,
                                                 @Artist ArtistJson artist) throws Exception {
        String updatedName = RandomDataUtils.randomName() + " " + RandomDataUtils.randomSurname();

        final ArtistJson updateRequest = new ArtistJson(
                artist.id(),
                updatedName,
                null,
                null
        );

        final ArtistJson updatedArtist = gatewayApiClient.updateArtist(
                bearer(token),
                updateRequest
        );

        assertEquals(artist.id(), updatedArtist.id());
        assertEquals(updatedName, updatedArtist.name());
        assertEquals(artist.biography(), updatedArtist.biography());
        assertEquals(artist.photo(), updatedArtist.photo());

        final ArtistJson result = gatewayApiClient.getArtistById(
                bearer(token),
                artist.id().toString()
        );

        assertEquals(artist.id(), result.id());
        assertEquals(updatedName, result.name());
        assertEquals(artist.biography(), result.biography());
        assertEquals(artist.photo(), result.photo());
    }

    @Test
    @ApiLogin(username = "TestDefaultUser")
    @Artist()
    @DisplayName("Should Update Artist Biography With Authorization")
    void shouldUpdateArtistBiographyWithAuthorization(@Token String token,
                                                      @Artist ArtistJson artist) throws Exception {
        String updatedBiography = RandomDataUtils.randomSentence(10);

        final ArtistJson updateRequest = new ArtistJson(
                artist.id(),
                null,
                updatedBiography,
                null
        );

        final ArtistJson updatedArtist = gatewayApiClient.updateArtist(
                bearer(token),
                updateRequest
        );

        assertEquals(artist.id(), updatedArtist.id());
        assertEquals(artist.name(), updatedArtist.name());
        assertEquals(updatedBiography, updatedArtist.biography());
        assertEquals(artist.photo(), updatedArtist.photo());

        final ArtistJson result = gatewayApiClient.getArtistById(
                bearer(token),
                artist.id().toString()
        );

        assertEquals(artist.id(), result.id());
        assertEquals(artist.name(), result.name());
        assertEquals(updatedBiography, result.biography());
        assertEquals(artist.photo(), result.photo());
    }

    @Test
    @ApiLogin(username = "TestDefaultUser")
    @Artist
    @DisplayName("Should Update Artist Photo With Authorization")
    void shouldUpdateArtistPhotoWithAuthorization(@Token String token,
                                                  @Artist ArtistJson artist) throws Exception {
        final ArtistJson updateRequest = new ArtistJson(
                artist.id(),
                null,
                null,
                IMAGE
        );

        final ArtistJson updatedArtist = gatewayApiClient.updateArtist(
                bearer(token),
                updateRequest
        );

        assertEquals(artist.id(), updatedArtist.id());
        assertEquals(artist.name(), updatedArtist.name());
        assertEquals(artist.biography(), updatedArtist.biography());
        assertEquals(IMAGE, updatedArtist.photo());

        final ArtistJson result = gatewayApiClient.getArtistById(
                bearer(token),
                artist.id().toString()
        );

        assertEquals(artist.id(), result.id());
        assertEquals(artist.name(), result.name());
        assertEquals(artist.biography(), result.biography());
        assertEquals(IMAGE, result.photo());
    }

    @Test
    @Artist
    @DisplayName("Should Not Update Artist Without Authorization")
    void shouldNotUpdateArtistWithoutAuthorization(@Artist ArtistJson artist) throws Exception {
        String updatedName = RandomDataUtils.randomName() + " " + RandomDataUtils.randomSurname();

        ArtistJson updateRequest = new ArtistJson(
                artist.id(),
                updatedName,
                null,
                null
        );

        Response<ArtistJson> response = gatewayApiClient.updateArtistRaw(null, updateRequest);

        assertEquals(401, response.code());

        ArtistJson actualArtist = gatewayApiClient.getArtistById(null, artist.id().toString());

        assertEquals(artist.id(), actualArtist.id());
        assertEquals(artist.name(), actualArtist.name());
        assertEquals(artist.biography(), actualArtist.biography());
        assertEquals(artist.photo(), actualArtist.photo());
    }

    @Test
    @ApiLogin(username = "TestDefaultUser")
    @DisplayName("Should Create Artist With Authorization")
    void shouldCreateArtistWithAuthorization(@Token String token) throws Exception {
        String name = RandomDataUtils.randomName() + " " + RandomDataUtils.randomSurname();
        String biography = RandomDataUtils.randomSentence(10);

        final ArtistJson request = new ArtistJson(
                null,
                name,
                biography,
                null
        );

        final ArtistJson result = gatewayApiClient.createArtist(
                bearer(token),
                request
        );

        assertNotNull(result.id());
        assertEquals(request.name(), result.name());
        assertEquals(request.biography(), result.biography());
        assertEquals(request.photo(), result.photo());
    }

    @Test
    @DisplayName("Should Not Create Artist Without Authorization")
    void shouldNotCreateArtistWithoutAuthorization() throws Exception {
        String name = RandomDataUtils.randomName() + " " + RandomDataUtils.randomSurname();
        String biography = RandomDataUtils.randomSentence(10);

        ArtistJson request = new ArtistJson(
                null,
                name,
                biography,
                null
        );

        long totalBefore = gatewayApiClient.getAllArtists(null, defaultPageable()).getTotalElements();

        Response<ArtistJson> response = gatewayApiClient.createArtistRaw(null, request);

        assertEquals(401, response.code());

        RestResponsePage<ArtistJson> result = gatewayApiClient.getAllArtists(null, defaultPageable());
        assertEquals(totalBefore, result.getTotalElements());
    }
}
