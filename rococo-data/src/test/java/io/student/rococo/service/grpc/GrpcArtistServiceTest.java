package io.student.rococo.service.grpc;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import io.student.rococo.data.entity.ArtistEntity;
import io.student.rococo.grpc.*;
import io.student.rococo.service.db.ArtistDbService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrpcArtistServiceTest {

    @Mock
    private ArtistDbService artistDbService;

    @Mock
    private StreamObserver<ArtistResponse> artistResponseObserver;

    @Mock
    private StreamObserver<ArtistsResponse> artistsResponseObserver;

    @InjectMocks
    private GrpcArtistService grpcArtistService;

    @Captor
    private ArgumentCaptor<ArtistResponse> artistResponseCaptor;

    @Captor
    private ArgumentCaptor<ArtistsResponse> artistsResponseCaptor;

    @Test
    void getArtistById() {
        final UUID id = UUID.randomUUID();
        final String name = "Michelangelo";
        final String biography = "Biography";
        final byte[] photo = "photo".getBytes();

        final ArtistEntity entity = createArtistEntity(id, name, biography, photo);

        when(artistDbService.getById(id.toString())).thenReturn(entity);

        final IdRequest request = IdRequest.newBuilder()
                .setId(id.toString())
                .build();

        grpcArtistService.getArtistById(request, artistResponseObserver);

        verify(artistResponseObserver).onNext(artistResponseCaptor.capture());
        verify(artistResponseObserver).onCompleted();

        final ArtistResponse response = artistResponseCaptor.getValue();
        assertEquals(id.toString(), response.getId());
        assertEquals(name, response.getName());
        assertEquals(biography, response.getBiography());
        assertArrayEquals(photo, response.getPhoto().toByteArray());
    }

    @Test
    void getArtistsByName() {
        final UUID firstId = UUID.randomUUID();
        final UUID secondId = UUID.randomUUID();

        final String searchName = "angelo";
        final String firstName = "Michelangelo";
        final String firstBiography = "Bio 1";
        final byte[] firstPhoto = "photo1".getBytes();

        final String secondName = "Angelo Bronzino";
        final String secondBiography = "Bio 2";
        final byte[] secondPhoto = "photo2".getBytes();

        final int page = 0;
        final int size = 2;
        final long totalElements = 2L;
        final int totalPages = 1;

        final ArtistEntity firstArtist = createArtistEntity(firstId, firstName, firstBiography, firstPhoto);
        final ArtistEntity secondArtist = createArtistEntity(secondId, secondName, secondBiography, secondPhoto);

        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<ArtistEntity> artistsPage = new PageImpl<>(List.of(firstArtist, secondArtist), pageRequest, totalElements);

        when(artistDbService.getByName(searchName, pageRequest)).thenReturn(artistsPage);

        final ArtistNameRequest request = ArtistNameRequest.newBuilder()
                .setName(searchName)
                .setPageable(PageableRequest.newBuilder()
                        .setPage(page)
                        .setSize(size)
                        .build())
                .build();

        grpcArtistService.getArtistsByName(request, artistsResponseObserver);

        verify(artistsResponseObserver).onNext(artistsResponseCaptor.capture());
        verify(artistsResponseObserver).onCompleted();

        final ArtistsResponse response = artistsResponseCaptor.getValue();
        assertEquals(2, response.getArtistsCount());

        assertEquals(firstId.toString(), response.getArtists(0).getId());
        assertEquals(firstName, response.getArtists(0).getName());
        assertEquals(firstBiography, response.getArtists(0).getBiography());
        assertArrayEquals(firstPhoto, response.getArtists(0).getPhoto().toByteArray());

        assertEquals(secondId.toString(), response.getArtists(1).getId());
        assertEquals(secondName, response.getArtists(1).getName());
        assertEquals(secondBiography, response.getArtists(1).getBiography());
        assertArrayEquals(secondPhoto, response.getArtists(1).getPhoto().toByteArray());

        assertEquals(page, response.getPage());
        assertEquals(size, response.getSize());
        assertEquals(totalElements, response.getTotalElements());
        assertEquals(totalPages, response.getTotalPages());

        verify(artistDbService).getByName(searchName, pageRequest);
    }

    @Test
    void getArtistsByNameShouldReturnEmptyPage() {
        final String searchName = "unknown";
        final int page = 0;
        final int size = 10;

        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<ArtistEntity> artistsPage = new PageImpl<>(List.of(), pageRequest, 0);

        when(artistDbService.getByName(searchName, pageRequest)).thenReturn(artistsPage);

        final ArtistNameRequest request = ArtistNameRequest.newBuilder()
                .setName(searchName)
                .setPageable(PageableRequest.newBuilder()
                        .setPage(page)
                        .setSize(size)
                        .build())
                .build();

        grpcArtistService.getArtistsByName(request, artistsResponseObserver);

        verify(artistsResponseObserver).onNext(artistsResponseCaptor.capture());
        verify(artistsResponseObserver).onCompleted();

        final ArtistsResponse response = artistsResponseCaptor.getValue();
        assertEquals(0, response.getArtistsCount());
        assertEquals(page, response.getPage());
        assertEquals(size, response.getSize());
        assertEquals(0, response.getTotalElements());
        assertEquals(0, response.getTotalPages());

        verify(artistDbService).getByName(searchName, pageRequest);
    }

    @Test
    void allArtists() {
        final UUID firstId = UUID.randomUUID();
        final UUID secondId = UUID.randomUUID();

        final String firstName = "Michelangelo";
        final String firstBiography = "Bio 1";
        final byte[] firstPhoto = "photo1".getBytes();

        final String secondName = "Donatello";
        final String secondBiography = "Bio 2";
        final byte[] secondPhoto = "photo2".getBytes();

        final int page = 1;
        final int size = 2;
        final long totalElements = 5L;
        final int totalPages = 3;

        final ArtistEntity firstArtist = createArtistEntity(firstId, firstName, firstBiography, firstPhoto);
        final ArtistEntity secondArtist = createArtistEntity(secondId, secondName, secondBiography, secondPhoto);

        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<ArtistEntity> artistsPage = new PageImpl<>(List.of(firstArtist, secondArtist), pageRequest, totalElements);

        when(artistDbService.getAll(pageRequest)).thenReturn(artistsPage);

        final PageableRequest request = PageableRequest.newBuilder()
                .setPage(page)
                .setSize(size)
                .build();

        grpcArtistService.allArtists(request, artistsResponseObserver);

        verify(artistsResponseObserver).onNext(artistsResponseCaptor.capture());
        verify(artistsResponseObserver).onCompleted();

        final ArtistsResponse response = artistsResponseCaptor.getValue();
        assertEquals(2, response.getArtistsCount());

        assertEquals(firstId.toString(), response.getArtists(0).getId());
        assertEquals(firstName, response.getArtists(0).getName());
        assertEquals(firstBiography, response.getArtists(0).getBiography());
        assertArrayEquals(firstPhoto, response.getArtists(0).getPhoto().toByteArray());

        assertEquals(secondId.toString(), response.getArtists(1).getId());
        assertEquals(secondName, response.getArtists(1).getName());
        assertEquals(secondBiography, response.getArtists(1).getBiography());
        assertArrayEquals(secondPhoto, response.getArtists(1).getPhoto().toByteArray());

        assertEquals(page, response.getPage());
        assertEquals(size, response.getSize());
        assertEquals(totalElements, response.getTotalElements());
        assertEquals(totalPages, response.getTotalPages());
    }

    @Test
    void createWithPhoto() {
        final UUID id = UUID.randomUUID();
        final String name = "Michelangelo";
        final String biography = "Biography";
        final byte[] photo = "photo".getBytes();

        final ArtistEntity result = createArtistEntity(id, name, biography, photo);

        when(artistDbService.create(name, biography, photo)).thenReturn(result);

        final CreateArtistRequest request = CreateArtistRequest.newBuilder()
                .setName(name)
                .setBiography(biography)
                .setPhoto(ByteString.copyFrom(photo))
                .build();

        grpcArtistService.createArtist(request, artistResponseObserver);

        verify(artistDbService).create(name, biography, photo);
        verify(artistResponseObserver).onNext(artistResponseCaptor.capture());
        verify(artistResponseObserver).onCompleted();

        final ArtistResponse response = artistResponseCaptor.getValue();
        assertEquals(id.toString(), response.getId());
        assertEquals(name, response.getName());
        assertEquals(biography, response.getBiography());
        assertArrayEquals(photo, response.getPhoto().toByteArray());
    }

    @Test
    void createWithoutPhoto() {
        final UUID id = UUID.randomUUID();
        final String name = "Michelangelo";
        final String biography = "Biography";
        final byte[] photo = null;

        final ArtistEntity result = createArtistEntity(id, name, biography, photo);

        when(artistDbService.create(name, biography, photo)).thenReturn(result);

        final CreateArtistRequest request = CreateArtistRequest.newBuilder()
                .setName(name)
                .setBiography(biography)
                .setPhoto(ByteString.EMPTY)
                .build();

        grpcArtistService.createArtist(request, artistResponseObserver);

        verify(artistDbService).create(name, biography, photo);
        verify(artistResponseObserver).onNext(artistResponseCaptor.capture());
        verify(artistResponseObserver).onCompleted();

        final ArtistResponse response = artistResponseCaptor.getValue();
        assertEquals(id.toString(), response.getId());
        assertEquals(name, response.getName());
        assertEquals(biography, response.getBiography());
        assertTrue(response.getPhoto().isEmpty());
    }

    @Test
    void updateAllFields() {
        final UUID id = UUID.randomUUID();
        final String name = "Michelangelo";
        final String biography = "Biography";
        final byte[] photo = "photo".getBytes();

        final ArtistEntity artistEntity = createArtistEntity(id, name, biography, photo);

        when(artistDbService.update(id.toString(), name, biography, photo)).thenReturn(artistEntity);

        final UpdateArtistRequest request = UpdateArtistRequest.newBuilder()
                .setId(id.toString())
                .setName(name)
                .setBiography(biography)
                .setPhoto(ByteString.copyFrom(photo))
                .build();

        grpcArtistService.updateArtist(request, artistResponseObserver);

        verify(artistDbService).update(id.toString(), name, biography, photo);
        verify(artistResponseObserver).onNext(artistResponseCaptor.capture());
        verify(artistResponseObserver).onCompleted();

        final ArtistResponse response = artistResponseCaptor.getValue();
        assertEquals(id.toString(), response.getId());
        assertEquals(name, response.getName());
        assertEquals(biography, response.getBiography());
        assertArrayEquals(photo, response.getPhoto().toByteArray());
    }

    @Test
    void updateOnlyId() {
        final UUID id = UUID.randomUUID();
        final String name = "Michelangelo";
        final String biography = "Biography";
        final String updateName = null;
        final String updateBiography = null;
        final byte[] updatePhoto = null;

        final ArtistEntity artistEntity = createArtistEntity(id, name, biography, null);

        when(artistDbService.update(id.toString(), updateName, updateBiography, updatePhoto)).thenReturn(artistEntity);

        final UpdateArtistRequest request = UpdateArtistRequest.newBuilder()
                .setId(id.toString())
                .build();

        grpcArtistService.updateArtist(request, artistResponseObserver);

        verify(artistDbService).update(id.toString(), updateName, updateBiography, updatePhoto);
        verify(artistResponseObserver).onNext(artistResponseCaptor.capture());
        verify(artistResponseObserver).onCompleted();

        final ArtistResponse response = artistResponseCaptor.getValue();
        assertEquals(id.toString(), response.getId());
        assertEquals(name, response.getName());
        assertEquals(biography, response.getBiography());
        assertTrue(response.getPhoto().isEmpty());
    }

    @Test
    void mapArtist() {
        final UUID id = UUID.randomUUID();
        final String name = "Michelangelo";
        final String biography = "Biography";
        final byte[] photo = "photo".getBytes();

        final ArtistEntity artistEntity = createArtistEntity(id, name, biography, photo);

        final ArtistResponse response = GrpcArtistService.artistEntityToArtistProtoResponse(artistEntity);

        assertEquals(id.toString(), response.getId());
        assertEquals(name, response.getName());
        assertEquals(biography, response.getBiography());
        assertArrayEquals(photo, response.getPhoto().toByteArray());
    }

    @Test
    void mapNullFields() {
        final UUID id = null;
        final String name = null;
        final String biography = null;
        final byte[] photo = null;

        final ArtistEntity artistEntity = createArtistEntity(id, name, biography, photo);

        final ArtistResponse response = GrpcArtistService.artistEntityToArtistProtoResponse(artistEntity);

        assertEquals("", response.getId());
        assertEquals("", response.getName());
        assertEquals("", response.getBiography());
        assertTrue(response.getPhoto().isEmpty());
    }

    @Test
    void mapNullPhoto() {
        final UUID id = UUID.randomUUID();
        final String name = "Michelangelo";
        final String biography = "Biography";
        final byte[] photo = null;

        final ArtistEntity artistEntity = createArtistEntity(id, name, biography, photo);

        final ArtistResponse response = GrpcArtistService.artistEntityToArtistProtoResponse(artistEntity);

        assertEquals(id.toString(), response.getId());
        assertEquals(name, response.getName());
        assertEquals(biography, response.getBiography());
        assertEquals(ByteString.EMPTY, response.getPhoto());
    }

    private ArtistEntity createArtistEntity(final UUID id, final String name, final String biography, final byte[] photo) {
        final ArtistEntity artist = new ArtistEntity();
        artist.setId(id);
        artist.setName(name);
        artist.setBiography(biography);
        artist.setPhoto(photo);
        return artist;
    }
}