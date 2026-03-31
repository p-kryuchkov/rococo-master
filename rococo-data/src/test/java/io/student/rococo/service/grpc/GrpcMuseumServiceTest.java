package io.student.rococo.service.grpc;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import io.student.rococo.data.entity.CountryEntity;
import io.student.rococo.data.entity.MuseumEntity;
import io.student.rococo.grpc.CreateMuseumRequest;
import io.student.rococo.grpc.Geo;
import io.student.rococo.grpc.IdRequest;
import io.student.rococo.grpc.MuseumResponse;
import io.student.rococo.grpc.MuseumsResponse;
import io.student.rococo.grpc.PageableRequest;
import io.student.rococo.grpc.UpdateMuseumRequest;
import io.student.rococo.service.db.MuseumDbService;
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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrpcMuseumServiceTest {

    @Mock
    private MuseumDbService museumDbService;

    @Mock
    private StreamObserver<MuseumResponse> museumResponseObserver;

    @Mock
    private StreamObserver<MuseumsResponse> museumsResponseObserver;

    @InjectMocks
    private GrpcMuseumService grpcMuseumService;

    @Captor
    private ArgumentCaptor<MuseumResponse> museumResponseCaptor;

    @Captor
    private ArgumentCaptor<MuseumsResponse> museumsResponseCaptor;

    @Test
    void allMuseums() {
        final UUID firstId = UUID.randomUUID();
        final UUID secondId = UUID.randomUUID();

        final UUID firstCountryId = UUID.randomUUID();
        final UUID secondCountryId = UUID.randomUUID();

        final String firstTitle = "Louvre";
        final String firstDescription = "Museum 1";
        final String firstCity = "Paris";
        final String firstCountryName = "France";
        final byte[] firstPhoto = "photo1".getBytes();

        final String secondTitle = "Prado";
        final String secondDescription = "Museum 2";
        final String secondCity = "Madrid";
        final String secondCountryName = "Spain";
        final byte[] secondPhoto = "photo2".getBytes();

        final int page = 1;
        final int size = 2;
        final long totalElements = 5L;
        final int totalPages = 3;

        final CountryEntity firstCountry = createCountryEntity(firstCountryId, firstCountryName);
        final CountryEntity secondCountry = createCountryEntity(secondCountryId, secondCountryName);

        final MuseumEntity firstMuseum = createMuseumEntity(firstId, firstTitle, firstDescription, firstCity, firstPhoto, firstCountry);
        final MuseumEntity secondMuseum = createMuseumEntity(secondId, secondTitle, secondDescription, secondCity, secondPhoto, secondCountry);

        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<MuseumEntity> museumsPage = new PageImpl<>(
                List.of(firstMuseum, secondMuseum),
                pageRequest,
                totalElements
        );

        when(museumDbService.getAll(pageRequest)).thenReturn(museumsPage);

        final PageableRequest request = PageableRequest.newBuilder()
                .setPage(page)
                .setSize(size)
                .build();

        grpcMuseumService.allMuseums(request, museumsResponseObserver);

        verify(museumDbService).getAll(pageRequest);
        verify(museumsResponseObserver).onNext(museumsResponseCaptor.capture());
        verify(museumsResponseObserver).onCompleted();

        final MuseumsResponse response = museumsResponseCaptor.getValue();

        assertEquals(2, response.getMuseumsCount());

        assertEquals(firstId.toString(), response.getMuseums(0).getId());
        assertEquals(firstTitle, response.getMuseums(0).getTitle());
        assertEquals(firstDescription, response.getMuseums(0).getDescription());
        assertArrayEquals(firstPhoto, response.getMuseums(0).getPhoto().toByteArray());
        assertEquals(firstCity, response.getMuseums(0).getGeo().getCity());
        assertEquals(firstCountryId.toString(), response.getMuseums(0).getGeo().getCountryId());
        assertEquals(firstCountryName, response.getMuseums(0).getGeo().getCountryName());

        assertEquals(secondId.toString(), response.getMuseums(1).getId());
        assertEquals(secondTitle, response.getMuseums(1).getTitle());
        assertEquals(secondDescription, response.getMuseums(1).getDescription());
        assertArrayEquals(secondPhoto, response.getMuseums(1).getPhoto().toByteArray());
        assertEquals(secondCity, response.getMuseums(1).getGeo().getCity());
        assertEquals(secondCountryId.toString(), response.getMuseums(1).getGeo().getCountryId());
        assertEquals(secondCountryName, response.getMuseums(1).getGeo().getCountryName());

        assertEquals(page, response.getPage());
        assertEquals(size, response.getSize());
        assertEquals(totalElements, response.getTotalElements());
        assertEquals(totalPages, response.getTotalPages());
    }

    @Test
    void findMuseumById() {
        final UUID id = UUID.randomUUID();
        final UUID countryId = UUID.randomUUID();

        final String title = "Louvre";
        final String description = "Museum";
        final String city = "Paris";
        final String countryName = "France";
        final byte[] photo = "photo".getBytes();

        final CountryEntity country = createCountryEntity(countryId, countryName);
        final MuseumEntity museum = createMuseumEntity(id, title, description, city, photo, country);

        when(museumDbService.getById(id.toString())).thenReturn(museum);

        final IdRequest request = IdRequest.newBuilder()
                .setId(id.toString())
                .build();

        grpcMuseumService.findMuseumById(request, museumResponseObserver);

        verify(museumDbService).getById(id.toString());
        verify(museumResponseObserver).onNext(museumResponseCaptor.capture());
        verify(museumResponseObserver).onCompleted();

        final MuseumResponse response = museumResponseCaptor.getValue();

        assertEquals(id.toString(), response.getId());
        assertEquals(title, response.getTitle());
        assertEquals(description, response.getDescription());
        assertArrayEquals(photo, response.getPhoto().toByteArray());
        assertEquals(city, response.getGeo().getCity());
        assertEquals(countryId.toString(), response.getGeo().getCountryId());
        assertEquals(countryName, response.getGeo().getCountryName());
    }

    @Test
    void createWithPhoto() {
        final UUID id = UUID.randomUUID();
        final UUID countryId = UUID.randomUUID();

        final String title = "Louvre";
        final String description = "Museum";
        final String city = "Paris";
        final String countryName = "France";
        final byte[] photo = "photo".getBytes();

        final CountryEntity country = createCountryEntity(countryId, countryName);
        final MuseumEntity museum = createMuseumEntity(id, title, description, city, photo, country);

        final Geo geo = Geo.newBuilder()
                .setCity(city)
                .setCountryId(countryId.toString())
                .build();

        when(museumDbService.create(title, description, city, countryId.toString(), photo)).thenReturn(museum);

        final CreateMuseumRequest request = CreateMuseumRequest.newBuilder()
                .setTitle(title)
                .setDescription(description)
                .setGeo(geo)
                .setPhoto(ByteString.copyFrom(photo))
                .build();

        grpcMuseumService.createMuseum(request, museumResponseObserver);

        verify(museumDbService).create(title, description, city, countryId.toString(), photo);
        verify(museumResponseObserver).onNext(museumResponseCaptor.capture());
        verify(museumResponseObserver).onCompleted();

        final MuseumResponse response = museumResponseCaptor.getValue();

        assertEquals(id.toString(), response.getId());
        assertEquals(title, response.getTitle());
        assertEquals(description, response.getDescription());
        assertArrayEquals(photo, response.getPhoto().toByteArray());
        assertEquals(city, response.getGeo().getCity());
        assertEquals(countryId.toString(), response.getGeo().getCountryId());
        assertEquals(countryName, response.getGeo().getCountryName());
    }

    @Test
    void createWithoutPhoto() {
        final UUID id = UUID.randomUUID();
        final UUID countryId = UUID.randomUUID();

        final String title = "Louvre";
        final String description = "Museum";
        final String city = "Paris";
        final String countryName = "France";
        final byte[] photo = null;

        final CountryEntity country = createCountryEntity(countryId, countryName);
        final MuseumEntity museum = createMuseumEntity(id, title, description, city, photo, country);

        final Geo geo = Geo.newBuilder()
                .setCity(city)
                .setCountryId(countryId.toString())
                .build();

        when(museumDbService.create(title, description, city, countryId.toString(), photo)).thenReturn(museum);

        final CreateMuseumRequest request = CreateMuseumRequest.newBuilder()
                .setTitle(title)
                .setDescription(description)
                .setGeo(geo)
                .setPhoto(ByteString.EMPTY)
                .build();

        grpcMuseumService.createMuseum(request, museumResponseObserver);

        verify(museumDbService).create(title, description, city, countryId.toString(), photo);
        verify(museumResponseObserver).onNext(museumResponseCaptor.capture());
        verify(museumResponseObserver).onCompleted();

        final MuseumResponse response = museumResponseCaptor.getValue();

        assertEquals(id.toString(), response.getId());
        assertEquals(title, response.getTitle());
        assertEquals(description, response.getDescription());
        assertTrue(response.getPhoto().isEmpty());
        assertEquals(city, response.getGeo().getCity());
        assertEquals(countryId.toString(), response.getGeo().getCountryId());
        assertEquals(countryName, response.getGeo().getCountryName());
    }

    @Test
    void updateAllFields() {
        final UUID id = UUID.randomUUID();
        final UUID countryId = UUID.randomUUID();

        final String title = "Louvre";
        final String description = "Museum";
        final String city = "Paris";
        final String countryName = "France";
        final byte[] photo = "photo".getBytes();

        final CountryEntity country = createCountryEntity(countryId, countryName);
        final MuseumEntity museum = createMuseumEntity(id, title, description, city, photo, country);

        final Geo geo = Geo.newBuilder()
                .setCity(city)
                .setCountryId(countryId.toString())
                .build();

        when(museumDbService.update(id.toString(), title, description, city, countryId.toString(), photo))
                .thenReturn(museum);

        final UpdateMuseumRequest request = UpdateMuseumRequest.newBuilder()
                .setId(id.toString())
                .setTitle(title)
                .setDescription(description)
                .setGeo(geo)
                .setPhoto(ByteString.copyFrom(photo))
                .build();

        grpcMuseumService.updateMuseum(request, museumResponseObserver);

        verify(museumDbService).update(id.toString(), title, description, city, countryId.toString(), photo);
        verify(museumResponseObserver).onNext(museumResponseCaptor.capture());
        verify(museumResponseObserver).onCompleted();

        final MuseumResponse response = museumResponseCaptor.getValue();

        assertEquals(id.toString(), response.getId());
        assertEquals(title, response.getTitle());
        assertEquals(description, response.getDescription());
        assertArrayEquals(photo, response.getPhoto().toByteArray());
        assertEquals(city, response.getGeo().getCity());
        assertEquals(countryId.toString(), response.getGeo().getCountryId());
        assertEquals(countryName, response.getGeo().getCountryName());
    }

    @Test
    void updateOnlyId() {
        final UUID id = UUID.randomUUID();
        final UUID countryId = UUID.randomUUID();

        final String title = "Louvre";
        final String description = "Museum";
        final String city = "Paris";
        final String countryName = "France";
        final String updateTitle = null;
        final String updateDescription = null;
        final String updateCity = null;
        final String updateCountryId = null;
        final byte[] updatePhoto = null;

        final CountryEntity country = createCountryEntity(countryId, countryName);
        final MuseumEntity museum = createMuseumEntity(id, title, description, city, null, country);

        when(museumDbService.update(id.toString(), updateTitle, updateDescription, updateCity, updateCountryId, updatePhoto))
                .thenReturn(museum);

        final UpdateMuseumRequest request = UpdateMuseumRequest.newBuilder()
                .setId(id.toString())
                .build();

        grpcMuseumService.updateMuseum(request, museumResponseObserver);

        verify(museumDbService).update(id.toString(), updateTitle, updateDescription, updateCity, updateCountryId, updatePhoto);
        verify(museumResponseObserver).onNext(museumResponseCaptor.capture());
        verify(museumResponseObserver).onCompleted();

        final MuseumResponse response = museumResponseCaptor.getValue();

        assertEquals(id.toString(), response.getId());
        assertEquals(title, response.getTitle());
        assertEquals(description, response.getDescription());
        assertTrue(response.getPhoto().isEmpty());
        assertEquals(city, response.getGeo().getCity());
        assertEquals(countryId.toString(), response.getGeo().getCountryId());
        assertEquals(countryName, response.getGeo().getCountryName());
    }

    @Test
    void mapMuseum() {
        final UUID id = UUID.randomUUID();
        final UUID countryId = UUID.randomUUID();

        final String title = "Louvre";
        final String description = "Museum";
        final String city = "Paris";
        final String countryName = "France";
        final byte[] photo = "photo".getBytes();

        final CountryEntity country = createCountryEntity(countryId, countryName);
        final MuseumEntity museum = createMuseumEntity(id, title, description, city, photo, country);

        final MuseumResponse response = GrpcMuseumService.museumEntityToMuseumProtoResponse(museum);

        assertEquals(id.toString(), response.getId());
        assertEquals(title, response.getTitle());
        assertEquals(description, response.getDescription());
        assertArrayEquals(photo, response.getPhoto().toByteArray());
        assertEquals(city, response.getGeo().getCity());
        assertEquals(countryId.toString(), response.getGeo().getCountryId());
        assertEquals(countryName, response.getGeo().getCountryName());
    }

    @Test
    void mapNullFields() {
        final UUID id = null;
        final String title = null;
        final String description = null;
        final String city = null;
        final byte[] photo = null;
        final CountryEntity country = null;

        final MuseumEntity museum = createMuseumEntity(id, title, description, city, photo, country);

        final MuseumResponse response = GrpcMuseumService.museumEntityToMuseumProtoResponse(museum);

        assertEquals("", response.getId());
        assertEquals("", response.getTitle());
        assertEquals("", response.getDescription());
        assertTrue(response.getPhoto().isEmpty());
        assertEquals("", response.getGeo().getCity());
        assertEquals("", response.getGeo().getCountryId());
        assertEquals("", response.getGeo().getCountryName());
    }

    @Test
    void mapNullPhoto() {
        final UUID id = UUID.randomUUID();
        final UUID countryId = UUID.randomUUID();

        final String title = "Louvre";
        final String description = "Museum";
        final String city = "Paris";
        final String countryName = "France";
        final byte[] photo = null;

        final CountryEntity country = createCountryEntity(countryId, countryName);
        final MuseumEntity museum = createMuseumEntity(id, title, description, city, photo, country);

        final MuseumResponse response = GrpcMuseumService.museumEntityToMuseumProtoResponse(museum);

        assertEquals(id.toString(), response.getId());
        assertEquals(title, response.getTitle());
        assertEquals(description, response.getDescription());
        assertEquals(ByteString.EMPTY, response.getPhoto());
        assertEquals(city, response.getGeo().getCity());
        assertEquals(countryId.toString(), response.getGeo().getCountryId());
        assertEquals(countryName, response.getGeo().getCountryName());
    }

    @Test
    void mapNullCountry() {
        final UUID id = UUID.randomUUID();

        final String title = "Louvre";
        final String description = "Museum";
        final String city = "Paris";
        final byte[] photo = "photo".getBytes();

        final MuseumEntity museum = createMuseumEntity(id, title, description, city, photo, null);

        final MuseumResponse response = GrpcMuseumService.museumEntityToMuseumProtoResponse(museum);

        assertEquals(id.toString(), response.getId());
        assertEquals(title, response.getTitle());
        assertEquals(description, response.getDescription());
        assertArrayEquals(photo, response.getPhoto().toByteArray());
        assertEquals(city, response.getGeo().getCity());
        assertEquals("", response.getGeo().getCountryId());
        assertEquals("", response.getGeo().getCountryName());
    }

    private MuseumEntity createMuseumEntity(
            final UUID id,
            final String title,
            final String description,
            final String city,
            final byte[] photo,
            final CountryEntity country
    ) {
        final MuseumEntity museum = new MuseumEntity();
        museum.setId(id);
        museum.setTitle(title);
        museum.setDescription(description);
        museum.setCity(city);
        museum.setPhoto(photo);
        museum.setCountry(country);
        return museum;
    }

    private CountryEntity createCountryEntity(final UUID id, final String name) {
        final CountryEntity country = new CountryEntity();
        country.setId(id);
        country.setName(name);
        return country;
    }
}