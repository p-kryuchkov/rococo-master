package io.student.rococo.api;

import io.student.rococo.model.*;
import io.student.rococo.model.page.RestResponsePage;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface GatewayApi {
    @GET("api/artist")
    Call<RestResponsePage<ArtistJson>> getAllArtists(@Header("Authorization") String bearerToken,
                                                     @Query("page") int page,
                                                     @Query("size") int size,
                                                     @Query("sort") List<String> sort);

    @GET("api/artist/{id}")
    Call<ArtistJson> getArtistById(@Header("Authorization") String bearerToken,
                                   @Path("id") String id);

    @POST("api/artist")
    Call<ArtistJson> createArtist(@Header("Authorization") String bearerToken,
                                  @Body ArtistJson artistJson);

    @PATCH("api/artist")
    Call<ArtistJson> updateArtist(@Header("Authorization") String bearerToken,
                                  @Body ArtistJson artistJson);

    @GET("api/country")
    Call<RestResponsePage<CountryJson>> getAllCountries(@Header("Authorization") String bearerToken,
                                                        @Query("page") int page,
                                                        @Query("size") int size,
                                                        @Query("sort") List<String> sort);

    @GET("api/museum")
    Call<RestResponsePage<MuseumJson>> getAllMuseums(@Header("Authorization") String bearerToken,
                                                     @Query("page") int page,
                                                     @Query("size") int size,
                                                     @Query("sort") List<String> sort);

    @GET("api/museum/{id}")
    Call<MuseumJson> getMuseumById(@Header("Authorization") String bearerToken,
                                   @Path("id") String id);

    @POST("api/museum")
    Call<MuseumJson> createMuseum(@Header("Authorization") String bearerToken,
                                  @Body MuseumJson museumJson);

    @PATCH("api/museum")
    Call<MuseumJson> updateMuseum(@Header("Authorization") String bearerToken,
                                  @Body MuseumJson museumJson);

    @GET("api/painting")
    Call<RestResponsePage<PaintingJson>> getAllPaintings(@Header("Authorization") String bearerToken,
                                                         @Query("page") int page,
                                                         @Query("size") int size,
                                                         @Query("sort") List<String> sort);

    @GET("api/painting/{id}")
    Call<PaintingJson> getPaintingById(@Header("Authorization") String bearerToken,
                                     @Path("id") String id);

    @GET("api/painting/author/{id}")
    Call<RestResponsePage<PaintingJson>> getAllPaintingsByArtist(@Header("Authorization") String bearerToken,
                                             @Query("page") int page,
                                             @Query("size") int size,
                                             @Query("sort") List<String> sort,
                                             @Path("id") String artistId);

    @POST("api/painting")
    Call<PaintingJson> createPainting(@Header("Authorization") String bearerToken,
                                    @Body PaintingJson paintingJson);

    @PATCH("api/painting")
    Call<PaintingJson> updatePainting(@Header("Authorization") String bearerToken,
                                    @Body PaintingJson paintingJson);

    @GET("api/session")
    Call<SessionJson> getSession(@Header("Authorization") String bearerToken);

    @GET("api/user")
    Call<UserJson> getCurrentUser(@Header("Authorization") String bearerToken);

    @PATCH("api/user")
    Call<UserJson> updateUser(@Header("Authorization") String bearerToken,
                                  @Body UserJson userJson);
}
