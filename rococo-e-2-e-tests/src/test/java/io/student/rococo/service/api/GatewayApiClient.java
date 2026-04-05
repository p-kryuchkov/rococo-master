package io.student.rococo.service.api;

import io.qameta.allure.Step;
import io.student.rococo.api.GatewayApi;
import io.student.rococo.config.Config;
import io.student.rococo.model.ArtistJson;
import io.student.rococo.model.CountryJson;
import io.student.rococo.model.MuseumJson;
import io.student.rococo.model.PaintingJson;
import io.student.rococo.model.SessionJson;
import io.student.rococo.model.UserJson;
import io.student.rococo.model.page.RestResponsePage;
import org.springframework.data.domain.Pageable;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GatewayApiClient extends RestClient {
    private final GatewayApi gatewayApi;
    private static final Config CFG = Config.getInstance();

    public GatewayApiClient(GatewayApi gatewayApi) {
        super(CFG.gatewayUrl());
        this.gatewayApi = gatewayApi;
    }

    @Step("Get all artists from gateway")
    public RestResponsePage<ArtistJson> getAllArtists(String bearerToken, Pageable pageable) {
        final Response<RestResponsePage<ArtistJson>> response;
        try {
            response = gatewayApi.getAllArtists(
                    bearerToken,
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    sort(pageable)
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }

    @Step("Get artist by id from gateway")
    public ArtistJson getArtistById(String bearerToken, String id) {
        final Response<ArtistJson> response;
        try {
            response = gatewayApi.getArtistById(
                    bearerToken,
                    id
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }

    @Step("Create artist in gateway")
    public ArtistJson createArtist(String bearerToken, ArtistJson artistJson) {
        final Response<ArtistJson> response;
        try {
            response = gatewayApi.createArtist(
                    bearerToken,
                    artistJson
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(201, response.code());
        return requireNonNull(response.body());
    }

    @Step("Update artist in gateway")
    public ArtistJson updateArtist(String bearerToken, ArtistJson artistJson) {
        final Response<ArtistJson> response;
        try {
            response = gatewayApi.updateArtist(
                    bearerToken,
                    artistJson
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }

    @Step("Get all countries from gateway")
    public RestResponsePage<CountryJson> getAllCountries(String bearerToken, Pageable pageable) {
        final Response<RestResponsePage<CountryJson>> response;
        try {
            response = gatewayApi.getAllCountries(
                    bearerToken,
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    sort(pageable)
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }

    @Step("Get all museums from gateway")
    public RestResponsePage<MuseumJson> getAllMuseums(String bearerToken, Pageable pageable) {
        final Response<RestResponsePage<MuseumJson>> response;
        try {
            response = gatewayApi.getAllMuseums(
                    bearerToken,
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    sort(pageable)
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }

    @Step("Get museum by id from gateway")
    public MuseumJson getMuseumById(String bearerToken, String id) {
        final Response<MuseumJson> response;
        try {
            response = gatewayApi.getMuseumById(
                    bearerToken,
                    id
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }

    @Step("Create museum in gateway")
    public MuseumJson createMuseum(String bearerToken, MuseumJson museumJson) {
        final Response<MuseumJson> response;
        try {
            response = gatewayApi.createMuseum(
                    bearerToken,
                    museumJson
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(201, response.code());
        return requireNonNull(response.body());
    }

    @Step("Update museum in gateway")
    public MuseumJson updateMuseum(String bearerToken, MuseumJson museumJson) {
        final Response<MuseumJson> response;
        try {
            response = gatewayApi.updateMuseum(
                    bearerToken,
                    museumJson
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }

    @Step("Get all paintings in gateway")
    public RestResponsePage<PaintingJson> getAllPaintings(String bearerToken, Pageable pageable) {
        final Response<RestResponsePage<PaintingJson>> response;
        try {
            response = gatewayApi.getAllPaintings(
                    bearerToken,
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    sort(pageable)
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }

    @Step("Get painting by id from gateway")
    public PaintingJson getPaintingById(String bearerToken, String id) {
        final Response<PaintingJson> response;
        try {
            response = gatewayApi.getPaintingById(
                    bearerToken,
                    id
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }

    @Step("Get all paintings by artist from gateway")
    public RestResponsePage<PaintingJson> getAllPaintingsByArtist(String bearerToken, String artistId, Pageable pageable) {
        final Response<RestResponsePage<PaintingJson>> response;
        try {
            response = gatewayApi.getAllPaintingsByArtist(
                    bearerToken,
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    sort(pageable),
                    artistId
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }

    @Step("Create painting in gateway")
    public PaintingJson createPainting(String bearerToken, PaintingJson paintingJson) {
        final Response<PaintingJson> response;
        try {
            response = gatewayApi.createPainting(
                    bearerToken,
                    paintingJson
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(201, response.code());
        return requireNonNull(response.body());
    }

    @Step("Update painting in gateway")
    public PaintingJson updatePainting(String bearerToken, PaintingJson paintingJson) {
        final Response<PaintingJson> response;
        try {
            response = gatewayApi.updatePainting(
                    bearerToken,
                    paintingJson
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }

    @Step("Get session from gateway")
    public SessionJson getSession(String bearerToken) {
        final Response<SessionJson> response;
        try {
            response = gatewayApi.getSession(
                    bearerToken
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }

    @Step("Get current user from gateway")
    public UserJson getCurrentUser(String bearerToken) {
        final Response<UserJson> response;
        try {
            response = gatewayApi.getCurrentUser(
                    bearerToken
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }

    @Step("Update user in gateway")
    public UserJson updateUser(String bearerToken, UserJson userJson) {
        final Response<UserJson> response;
        try {
            response = gatewayApi.updateUser(
                    bearerToken,
                    userJson
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }

    private List<String> sort(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            return List.of();
        }

        return pageable.getSort().stream()
                .map(order -> order.getProperty() + "," + order.getDirection().name().toLowerCase())
                .toList();
    }
}