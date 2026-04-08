package io.student.rococo.test.rest;

import io.student.rococo.jupiter.annotation.*;
import io.student.rococo.jupiter.annotation.meta.RestTest;
import io.student.rococo.model.ArtistJson;
import io.student.rococo.model.MuseumJson;
import io.student.rococo.model.PaintingJson;
import io.student.rococo.model.page.RestResponsePage;
import io.student.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import static org.junit.jupiter.api.Assertions.*;

@RestTest
public class PaintingApiTest extends BaseGatewayApiTest {

    @Test
    @ApiLogin(username = "TestDefaultUser")
    @DisplayName("Should Return Painting Page With Authorization")
    void shouldReturnPaintingsPageWithAuthorization(@Token String token) throws Exception {
        final RestResponsePage<PaintingJson> result = gatewayApiClient.getAllPaintings(
                bearer(token),
                defaultPageable()
        );

        assertTrue(result.hasContent());
        assertFalse(result.getContent().isEmpty());
    }

    @Test
    @DisplayName("Should Return Painting Page Without Authorization")
    void shouldReturnPaintingsPageWithoutAuthorization() throws Exception {
        final RestResponsePage<PaintingJson> result = gatewayApiClient.getAllPaintings(
                null,
                defaultPageable()
        );

        assertTrue(result.hasContent());
        assertFalse(result.getContent().isEmpty());
    }

    @Test
    @DisplayName("Should Return Painting Pageable Correct")
    void shouldReturnPaintingsPageable() throws Exception {
        int page = 0;
        int size = 4;

        final RestResponsePage<PaintingJson> result = gatewayApiClient.getAllPaintings(
                null,
                pageable(page, size)
        );

        assertEquals(size, result.getSize());
        assertEquals(page, result.getNumber());
        assertTrue(result.getTotalElements() >= result.getContent().size());
        assertFalse(result.getContent().isEmpty());
    }

    @Test
    @DisplayName("Should Return Painting Default Pageable")
    void shouldReturnPaintingsDefaultPageable() throws Exception {
        int page = 0;
        int size = 10;

        final RestResponsePage<PaintingJson> result = gatewayApiClient.getAllPaintings(
                null,
                null
        );

        assertEquals(size, result.getSize());
        assertEquals(page, result.getNumber());
        assertTrue(result.getTotalElements() >= result.getContent().size());
        assertFalse(result.getContent().isEmpty());
    }

    @Test
    @ApiLogin(username = "TestDefaultUser")
    @Painting
    @DisplayName("Should Return Painting By Id With Authorization")
    void shouldReturnPaintingByIdWithAuthorization(@Token String token,
                                                   @Painting PaintingJson painting) throws Exception {
        final PaintingJson result = gatewayApiClient.getPaintingById(
                bearer(token),
                painting.id().toString()
        );

        assertEquals(painting.id(), result.id());
        assertEquals(painting.description(), result.description());
        assertEquals(painting.title(), result.title());
        assertEquals(painting.content(), result.content());
        assertEquals(painting.artist(), result.artist());
        assertEquals(painting.museum(), result.museum());
    }

    @Test
    @Painting
    @DisplayName("Should Return Painting By Id Without Authorization")
    void shouldReturnPaintingByIdWithoutAuthorization(@Painting PaintingJson painting) throws Exception {
        final PaintingJson result = gatewayApiClient.getPaintingById(
                null,
                painting.id().toString()
        );

        assertEquals(painting.id(), result.id());
        assertEquals(painting.description(), result.description());
        assertEquals(painting.title(), result.title());
        assertEquals(painting.content(), result.content());
        assertEquals(painting.artist(), result.artist());
        assertEquals(painting.museum(), result.museum());
    }

    @Test
    @ApiLogin(username = "TestDefaultUser")
    @Painting(artist = @Artist(name = "Илья Репин"))
    @DisplayName("Should Return Paintings By Artist With Authorization")
    void shouldReturnPaintingsByArtistWithAuthorization(@Token String token,
                                                        @Painting PaintingJson painting) throws Exception {
        final RestResponsePage<PaintingJson> result = gatewayApiClient.getAllPaintingsByArtist(
                bearer(token),
                painting.artist().id().toString(),
                defaultPageable()
        );

        assertTrue(result.hasContent());
        assertFalse(result.getContent().isEmpty());
        assertTrue(result.getContent().stream()
                .allMatch(p -> p.artist() != null && p.artist().id().equals(painting.artist().id())));
    }

    @Test
    @Painting
    @DisplayName("Should Return Paintings By Artist Without Authorization")
    void shouldReturnPaintingsByArtistWithoutAuthorization(@Painting PaintingJson painting) throws Exception {
        final RestResponsePage<PaintingJson> result = gatewayApiClient.getAllPaintingsByArtist(
                null,
                painting.artist().id().toString(),
                defaultPageable()
        );

        assertTrue(result.hasContent());
        assertFalse(result.getContent().isEmpty());
        assertTrue(result.getContent().stream()
                .allMatch(p -> p.artist() != null && p.artist().id().equals(painting.artist().id())));
        assertTrue(result.getContent().stream()
                .anyMatch(p -> p.id().equals(painting.id())));
    }

    @Test
    @Painting
    @DisplayName("Should Return Paintings By Artist Pageable Correct")
    void shouldReturnPaintingsByArtistPageable(@Painting PaintingJson painting) throws Exception {
        int page = 0;
        int size = 1;

        final RestResponsePage<PaintingJson> result = gatewayApiClient.getAllPaintingsByArtist(
                null,
                painting.artist().id().toString(),
                pageable(page, size)
        );

        assertEquals(size, result.getSize());
        assertEquals(page, result.getNumber());
        assertTrue(result.getTotalElements() >= 1);
        assertFalse(result.getContent().isEmpty());
        assertTrue(result.getContent().stream()
                .allMatch(p -> p.artist() != null && p.artist().id().equals(painting.artist().id())));
    }

    @Test
    @Painting(museum = @Museum(title = "Эрмитаж"),
            artist = @Artist(name = "Шишкин"))
    @DisplayName("Should Return Paintings By Artist Default Pageable")
    void shouldReturnPaintingsByArtistDefaultPageable(@Painting PaintingJson painting) throws Exception {
        int page = 0;
        int size = 10;

        final RestResponsePage<PaintingJson> result = gatewayApiClient.getAllPaintingsByArtist(
                null,
                painting.artist().id().toString(),
                null
        );

        assertEquals(size, result.getSize());
        assertEquals(page, result.getNumber());
        assertTrue(result.getTotalElements() >= 1);
        assertFalse(result.getContent().isEmpty());
        assertTrue(result.getContent().stream()
                .allMatch(p -> p.artist() != null && p.artist().id().equals(painting.artist().id())));
    }

    @Test
    @ApiLogin(username = "TestDefaultUser")
    @Painting
    @DisplayName("Should Update Painting Description With Authorization")
    void shouldUpdatePaintingDescriptionWithAuthorization(@Token String token,
                                                          @Painting PaintingJson painting) throws Exception {
        String updatedDescription = RandomDataUtils.randomSentence(10);

        final PaintingJson updateRequest = new PaintingJson(
                painting.id(),
                updatedDescription,
                null,
                null,
                null,
                null
        );

        final PaintingJson updatedPainting = gatewayApiClient.updatePainting(
                bearer(token),
                updateRequest
        );

        assertEquals(painting.id(), updatedPainting.id());
        assertEquals(updatedDescription, updatedPainting.description());
        assertEquals(painting.title(), updatedPainting.title());
        assertEquals(painting.content(), updatedPainting.content());
        assertEquals(painting.artist(), updatedPainting.artist());
        assertEquals(painting.museum(), updatedPainting.museum());

        final PaintingJson result = gatewayApiClient.getPaintingById(
                bearer(token),
                painting.id().toString()
        );

        assertEquals(painting.id(), result.id());
        assertEquals(updatedDescription, result.description());
        assertEquals(painting.title(), result.title());
        assertEquals(painting.content(), result.content());
        assertEquals(painting.artist(), result.artist());
        assertEquals(painting.museum(), result.museum());
    }

    @Test
    @ApiLogin(username = "TestDefaultUser")
    @Painting
    @DisplayName("Should Update Painting Title With Authorization")
    void shouldUpdatePaintingTitleWithAuthorization(@Token String token,
                                                    @Painting PaintingJson painting) throws Exception {
        String updatedTitle = RandomDataUtils.randomSentence(3);

        final PaintingJson updateRequest = new PaintingJson(
                painting.id(),
                null,
                updatedTitle,
                null,
                null,
                null
        );

        final PaintingJson updatedPainting = gatewayApiClient.updatePainting(
                bearer(token),
                updateRequest
        );

        assertEquals(painting.id(), updatedPainting.id());
        assertEquals(painting.description(), updatedPainting.description());
        assertEquals(updatedTitle, updatedPainting.title());
        assertEquals(painting.content(), updatedPainting.content());
        assertEquals(painting.artist(), updatedPainting.artist());
        assertEquals(painting.museum(), updatedPainting.museum());

        final PaintingJson result = gatewayApiClient.getPaintingById(
                bearer(token),
                painting.id().toString()
        );

        assertEquals(painting.id(), result.id());
        assertEquals(painting.description(), result.description());
        assertEquals(updatedTitle, result.title());
        assertEquals(painting.content(), result.content());
        assertEquals(painting.artist(), result.artist());
        assertEquals(painting.museum(), result.museum());
    }

    @Test
    @ApiLogin(username = "TestDefaultUser")
    @Painting
    @DisplayName("Should Update Painting Content With Authorization")
    void shouldUpdatePaintingContentWithAuthorization(@Token String token,
                                                      @Painting PaintingJson painting) throws Exception {
        final PaintingJson updateRequest = new PaintingJson(
                painting.id(),
                null,
                null,
                IMAGE,
                null,
                null
        );

        final PaintingJson updatedPainting = gatewayApiClient.updatePainting(
                bearer(token),
                updateRequest
        );

        assertEquals(painting.id(), updatedPainting.id());
        assertEquals(painting.description(), updatedPainting.description());
        assertEquals(painting.title(), updatedPainting.title());
        assertEquals(IMAGE, updatedPainting.content());
        assertEquals(painting.artist(), updatedPainting.artist());
        assertEquals(painting.museum(), updatedPainting.museum());

        final PaintingJson result = gatewayApiClient.getPaintingById(
                bearer(token),
                painting.id().toString()
        );

        assertEquals(painting.id(), result.id());
        assertEquals(painting.description(), result.description());
        assertEquals(painting.title(), result.title());
        assertEquals(IMAGE, result.content());
        assertEquals(painting.artist(), result.artist());
        assertEquals(painting.museum(), result.museum());
    }

    @Test
    @ApiLogin(username = "TestDefaultUser")
    @Painting
    @Museum
    @DisplayName("Should Update Painting Museum With Authorization")
    void shouldUpdatePaintingMuseumWithAuthorization(@Token String token,
                                                     @Painting PaintingJson painting,
                                                     @Museum MuseumJson museum) throws Exception {
        final PaintingJson updateRequest = new PaintingJson(
                painting.id(),
                null,
                null,
                null,
                null,
                museum
        );

        final PaintingJson updatedPainting = gatewayApiClient.updatePainting(
                bearer(token),
                updateRequest
        );

        assertEquals(painting.id(), updatedPainting.id());
        assertEquals(painting.description(), updatedPainting.description());
        assertEquals(painting.title(), updatedPainting.title());
        assertEquals(painting.content(), updatedPainting.content());
        assertEquals(painting.artist(), updatedPainting.artist());
        assertEquals(museum, updatedPainting.museum());

        final PaintingJson result = gatewayApiClient.getPaintingById(
                bearer(token),
                painting.id().toString()
        );

        assertEquals(painting.id(), result.id());
        assertEquals(painting.description(), result.description());
        assertEquals(painting.title(), result.title());
        assertEquals(painting.content(), result.content());
        assertEquals(painting.artist(), result.artist());
        assertEquals(museum, result.museum());
    }

    @Test
    @ApiLogin(username = "TestDefaultUser")
    @Painting
    @Artist
    @DisplayName("Should Update Painting Artist With Authorization")
    void shouldUpdatePaintingArtistWithAuthorization(@Token String token,
                                                     @Painting PaintingJson painting,
                                                     @Artist ArtistJson artist) throws Exception {
        final PaintingJson updateRequest = new PaintingJson(
                painting.id(),
                null,
                null,
                null,
                artist,
                null
        );

        final PaintingJson updatedPainting = gatewayApiClient.updatePainting(
                bearer(token),
                updateRequest
        );

        assertEquals(painting.id(), updatedPainting.id());
        assertEquals(painting.description(), updatedPainting.description());
        assertEquals(painting.title(), updatedPainting.title());
        assertEquals(painting.content(), updatedPainting.content());
        assertEquals(artist, updatedPainting.artist());
        assertEquals(painting.museum(), updatedPainting.museum());

        final PaintingJson result = gatewayApiClient.getPaintingById(
                bearer(token),
                painting.id().toString()
        );

        assertEquals(painting.id(), result.id());
        assertEquals(painting.description(), result.description());
        assertEquals(painting.title(), result.title());
        assertEquals(painting.content(), result.content());
        assertEquals(artist, result.artist());
        assertEquals(painting.museum(), result.museum());
    }

    @Test
    @Painting
    @DisplayName("Should Not Update Painting Without Authorization")
    void shouldNotUpdatePaintingWithoutAuthorization(@Painting PaintingJson painting) throws Exception {
        String updatedTitle = RandomDataUtils.randomSentence(3);

        final PaintingJson updateRequest = new PaintingJson(
                painting.id(),
                null,
                updatedTitle,
                null,
                null,
                null
        );

        Response<PaintingJson> response = gatewayApiClient.updatePaintingRaw(null, updateRequest);

        assertEquals(401, response.code());

        final PaintingJson actualPainting = gatewayApiClient.getPaintingById(
                null,
                painting.id().toString()
        );

        assertEquals(painting.id(), actualPainting.id());
        assertEquals(painting.description(), actualPainting.description());
        assertEquals(painting.title(), actualPainting.title());
        assertEquals(painting.content(), actualPainting.content());
        assertEquals(painting.artist(), actualPainting.artist());
        assertEquals(painting.museum(), actualPainting.museum());
    }

    @Test
    @ApiLogin(username = "TestDefaultUser")
    @Artist
    @Museum
    @DisplayName("Should Create Painting With Authorization")
    void shouldCreatePaintingWithAuthorization(@Token String token,
                                               @Artist ArtistJson artist,
                                               @Museum MuseumJson museum) throws Exception {
        String description = RandomDataUtils.randomSentence(10);
        String title = RandomDataUtils.randomSentence(3);

        final PaintingJson request = new PaintingJson(
                null,
                description,
                title,
                IMAGE,
                artist,
                museum
        );

        final PaintingJson result = gatewayApiClient.createPainting(
                bearer(token),
                request
        );

        assertNotNull(result.id());
        assertEquals(request.description(), result.description());
        assertEquals(request.title(), result.title());
        assertEquals(request.content(), result.content());
        assertEquals(request.artist(), result.artist());
        assertEquals(request.museum(), result.museum());
    }

    @Test
    @Artist
    @Museum
    @DisplayName("Should Not Create Painting Without Authorization")
    void shouldNotCreatePaintingWithoutAuthorization(@Artist ArtistJson artist,
                                                     @Museum MuseumJson museum) throws Exception {
        String description = RandomDataUtils.randomSentence(10);
        String title = RandomDataUtils.randomSentence(3);

        final PaintingJson request = new PaintingJson(
                null,
                description,
                title,
                IMAGE,
                artist,
                museum
        );

        long totalBefore = gatewayApiClient.getAllPaintings(null, defaultPageable()).getTotalElements();

        Response<PaintingJson> response = gatewayApiClient.createPaintingRaw(null, request);

        assertEquals(401, response.code());

        final RestResponsePage<PaintingJson> result = gatewayApiClient.getAllPaintings(
                null,
                defaultPageable()
        );

        assertEquals(totalBefore, result.getTotalElements());
    }
}