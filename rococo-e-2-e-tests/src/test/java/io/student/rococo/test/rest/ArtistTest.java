package io.student.rococo.test.rest;

import io.student.rococo.jupiter.annotation.Artist;
import io.student.rococo.jupiter.annotation.meta.RestTest;
import io.student.rococo.model.ArtistJson;
import io.student.rococo.service.api.GatewayApiClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RestTest
public class ArtistTest {
    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();

    @Test
    @Artist
    public void getArtistTest(ArtistJson artistJson) {
        final ArtistJson result = gatewayApiClient.getArtistById(null, artistJson.id().toString());
        assertEquals(artistJson.name(), result.name());
    }
}
