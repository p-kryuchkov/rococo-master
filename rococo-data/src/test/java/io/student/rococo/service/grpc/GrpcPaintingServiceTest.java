package io.student.rococo.service.grpc;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import io.student.rococo.data.entity.ArtistEntity;
import io.student.rococo.data.entity.MuseumEntity;
import io.student.rococo.data.entity.PaintingEntity;
import io.student.rococo.grpc.*;
import io.student.rococo.service.db.PaintingDbService;
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
class GrpcPaintingServiceTest {

    @Mock
    private PaintingDbService paintingDbService;

    @Mock
    private StreamObserver<PaintingResponse> paintingResponseObserver;

    @Mock
    private StreamObserver<PaintingsResponse> paintingsResponseObserver;

    @InjectMocks
    private GrpcPaintingService grpcPaintingService;

    @Captor
    private ArgumentCaptor<PaintingResponse> paintingResponseCaptor;

    @Captor
    private ArgumentCaptor<PaintingsResponse> paintingsResponseCaptor;

    @Test
    void findPaintingById() {
        final UUID paintingId = UUID.randomUUID();
        final UUID artistId = UUID.randomUUID();
        final UUID museumId = UUID.randomUUID();

        final String title = "The Creation of Adam";
        final String description = "Description";
        final byte[] content = "content".getBytes();

        final ArtistEntity artist = createArtistEntity(artistId, "Michelangelo", "Biography", "artist-photo".getBytes());
        final MuseumEntity museum = createMuseumEntity(museumId, "Sistine Chapel", "Museum description", "Rome", "museum-photo".getBytes());
        final PaintingEntity painting = createPaintingEntity(paintingId, title, description, content, artist, museum);

        when(paintingDbService.getById(paintingId.toString())).thenReturn(painting);

        final IdRequest request = IdRequest.newBuilder()
                .setId(paintingId.toString())
                .build();

        grpcPaintingService.findPaintingById(request, paintingResponseObserver);

        verify(paintingResponseObserver).onNext(paintingResponseCaptor.capture());
        verify(paintingResponseObserver).onCompleted();

        final PaintingResponse response = paintingResponseCaptor.getValue();
        assertEquals(paintingId.toString(), response.getId());
        assertEquals(title, response.getTitle());
        assertEquals(description, response.getDescription());
        assertArrayEquals(content, response.getContent().toByteArray());

        assertEquals(artistId.toString(), response.getArtist().getId());
        assertEquals("Michelangelo", response.getArtist().getName());
        assertEquals("Biography", response.getArtist().getBiography());

        assertEquals(museumId.toString(), response.getMuseum().getId());
        assertEquals("Sistine Chapel", response.getMuseum().getTitle());
        assertEquals("Museum description", response.getMuseum().getDescription());
    }

    @Test
    void allPaintings() {
        final UUID firstPaintingId = UUID.randomUUID();
        final UUID secondPaintingId = UUID.randomUUID();
        final UUID artistId = UUID.randomUUID();
        final UUID museumId = UUID.randomUUID();

        final int page = 1;
        final int size = 2;
        final long totalElements = 5L;
        final int totalPages = 3;

        final ArtistEntity artist = createArtistEntity(artistId, "Michelangelo", "Biography", "artist-photo".getBytes());
        final MuseumEntity museum = createMuseumEntity(museumId, "Louvre", "Museum description", "Paris", "museum-photo".getBytes());

        final PaintingEntity firstPainting = createPaintingEntity(
                firstPaintingId,
                "Painting 1",
                "Description 1",
                "content1".getBytes(),
                artist,
                museum
        );

        final PaintingEntity secondPainting = createPaintingEntity(
                secondPaintingId,
                "Painting 2",
                "Description 2",
                "content2".getBytes(),
                artist,
                museum
        );

        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<PaintingEntity> paintingsPage = new PageImpl<>(
                List.of(firstPainting, secondPainting),
                pageRequest,
                totalElements
        );

        when(paintingDbService.getAll(pageRequest)).thenReturn(paintingsPage);

        final PageableRequest request = PageableRequest.newBuilder()
                .setPage(page)
                .setSize(size)
                .build();

        grpcPaintingService.allPaintings(request, paintingsResponseObserver);

        verify(paintingsResponseObserver).onNext(paintingsResponseCaptor.capture());
        verify(paintingsResponseObserver).onCompleted();

        final PaintingsResponse response = paintingsResponseCaptor.getValue();
        assertEquals(2, response.getPaintingsCount());

        assertEquals(firstPaintingId.toString(), response.getPaintings(0).getId());
        assertEquals("Painting 1", response.getPaintings(0).getTitle());
        assertEquals("Description 1", response.getPaintings(0).getDescription());
        assertArrayEquals("content1".getBytes(), response.getPaintings(0).getContent().toByteArray());

        assertEquals(secondPaintingId.toString(), response.getPaintings(1).getId());
        assertEquals("Painting 2", response.getPaintings(1).getTitle());
        assertEquals("Description 2", response.getPaintings(1).getDescription());
        assertArrayEquals("content2".getBytes(), response.getPaintings(1).getContent().toByteArray());

        assertEquals(page, response.getPage());
        assertEquals(size, response.getSize());
        assertEquals(totalElements, response.getTotalElements());
        assertEquals(totalPages, response.getTotalPages());
    }

    @Test
    void findPaintingByArtist() {
        final UUID paintingId = UUID.randomUUID();
        final UUID artistId = UUID.randomUUID();

        final int page = 0;
        final int size = 1;
        final long totalElements = 1L;
        final int totalPages = 1;

        final ArtistEntity artist = createArtistEntity(artistId, "Michelangelo", "Biography", "artist-photo".getBytes());
        final PaintingEntity painting = createPaintingEntity(
                paintingId,
                "The Last Judgment",
                "Description",
                "content".getBytes(),
                artist,
                null
        );

        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<PaintingEntity> paintingsPage = new PageImpl<>(List.of(painting), pageRequest, totalElements);

        when(paintingDbService.getByArtistId(artistId.toString(), pageRequest)).thenReturn(paintingsPage);

        final PaintingsByArtistRequest request = PaintingsByArtistRequest.newBuilder()
                .setArtistId(artistId.toString())
                .setPageable(PageableRequest.newBuilder()
                        .setPage(page)
                        .setSize(size)
                        .build())
                .build();

        grpcPaintingService.findPaintingByArtist(request, paintingsResponseObserver);

        verify(paintingDbService).getByArtistId(artistId.toString(), pageRequest);
        verify(paintingsResponseObserver).onNext(paintingsResponseCaptor.capture());
        verify(paintingsResponseObserver).onCompleted();

        final PaintingsResponse response = paintingsResponseCaptor.getValue();
        assertEquals(1, response.getPaintingsCount());
        assertEquals(paintingId.toString(), response.getPaintings(0).getId());
        assertEquals("The Last Judgment", response.getPaintings(0).getTitle());
        assertEquals("Description", response.getPaintings(0).getDescription());
        assertArrayEquals("content".getBytes(), response.getPaintings(0).getContent().toByteArray());

        assertEquals(page, response.getPage());
        assertEquals(size, response.getSize());
        assertEquals(totalElements, response.getTotalElements());
        assertEquals(totalPages, response.getTotalPages());
    }

    @Test
    void createAllFields() {
        final UUID paintingId = UUID.randomUUID();
        final UUID artistId = UUID.randomUUID();
        final UUID museumId = UUID.randomUUID();

        final String title = "The Creation of Adam";
        final String description = "Description";
        final byte[] content = "content".getBytes();

        final ArtistEntity artist = createArtistEntity(artistId, "Michelangelo", "Biography", "artist-photo".getBytes());
        final MuseumEntity museum = createMuseumEntity(museumId, "Vatican Museums", "Museum description", "Rome", "museum-photo".getBytes());
        final PaintingEntity result = createPaintingEntity(paintingId, title, description, content, artist, museum);

        when(paintingDbService.create(
                title,
                description,
                content,
                artistId.toString(),
                museumId.toString()
        )).thenReturn(result);

        final CreatePaintingRequest request = CreatePaintingRequest.newBuilder()
                .setTitle(title)
                .setDescription(description)
                .setContent(ByteString.copyFrom(content))
                .setArtist(ArtistRequest.newBuilder().setId(artistId.toString()).build())
                .setMuseum(MuseumRequest.newBuilder().setId(museumId.toString()).build())
                .build();

        grpcPaintingService.createPainting(request, paintingResponseObserver);

        verify(paintingDbService).create(
                title,
                description,
                content,
                artistId.toString(),
                museumId.toString()
        );
        verify(paintingResponseObserver).onNext(paintingResponseCaptor.capture());
        verify(paintingResponseObserver).onCompleted();

        final PaintingResponse response = paintingResponseCaptor.getValue();
        assertEquals(paintingId.toString(), response.getId());
        assertEquals(title, response.getTitle());
        assertEquals(description, response.getDescription());
        assertArrayEquals(content, response.getContent().toByteArray());

        assertEquals(artistId.toString(), response.getArtist().getId());
        assertEquals(museumId.toString(), response.getMuseum().getId());
    }

    @Test
    void createWithoutOptionalFields() {
        final UUID paintingId = UUID.randomUUID();
        final String title = "The Creation of Adam";
        final String description = "Description";

        final PaintingEntity result = createPaintingEntity(paintingId, title, description, null, null, null);

        when(paintingDbService.create(
                title,
                description,
                null,
                null,
                null
        )).thenReturn(result);

        final CreatePaintingRequest request = CreatePaintingRequest.newBuilder()
                .setTitle(title)
                .setDescription(description)
                .setContent(ByteString.EMPTY)
                .build();

        grpcPaintingService.createPainting(request, paintingResponseObserver);

        verify(paintingDbService).create(
                title,
                description,
                null,
                null,
                null
        );
        verify(paintingResponseObserver).onNext(paintingResponseCaptor.capture());
        verify(paintingResponseObserver).onCompleted();

        final PaintingResponse response = paintingResponseCaptor.getValue();
        assertEquals(paintingId.toString(), response.getId());
        assertEquals(title, response.getTitle());
        assertEquals(description, response.getDescription());
        assertTrue(response.getContent().isEmpty());
        assertEquals(ArtistResponse.getDefaultInstance(), response.getArtist());
        assertEquals(MuseumResponse.getDefaultInstance(), response.getMuseum());
    }

    @Test
    void updateAllFields() {
        final UUID paintingId = UUID.randomUUID();
        final UUID artistId = UUID.randomUUID();
        final UUID museumId = UUID.randomUUID();

        final String title = "Updated title";
        final String description = "Updated description";
        final byte[] content = "updated-content".getBytes();

        final ArtistEntity artist = createArtistEntity(artistId, "Michelangelo", "Biography", "artist-photo".getBytes());
        final MuseumEntity museum = createMuseumEntity(museumId, "Uffizi", "Museum description", "Florence", "museum-photo".getBytes());
        final PaintingEntity updatedPainting = createPaintingEntity(paintingId, title, description, content, artist, museum);

        when(paintingDbService.update(
                paintingId.toString(),
                title,
                description,
                content,
                artistId.toString(),
                museumId.toString()
        )).thenReturn(updatedPainting);

        final UpdatePaintingRequest request = UpdatePaintingRequest.newBuilder()
                .setId(paintingId.toString())
                .setTitle(title)
                .setDescription(description)
                .setContent(ByteString.copyFrom(content))
                .setArtist(ArtistRequest.newBuilder().setId(artistId.toString()).build())
                .setMuseum(MuseumRequest.newBuilder().setId(museumId.toString()).build())
                .build();

        grpcPaintingService.updatePainting(request, paintingResponseObserver);

        verify(paintingDbService).update(
                paintingId.toString(),
                title,
                description,
                content,
                artistId.toString(),
                museumId.toString()
        );
        verify(paintingResponseObserver).onNext(paintingResponseCaptor.capture());
        verify(paintingResponseObserver).onCompleted();

        final PaintingResponse response = paintingResponseCaptor.getValue();
        assertEquals(paintingId.toString(), response.getId());
        assertEquals(title, response.getTitle());
        assertEquals(description, response.getDescription());
        assertArrayEquals(content, response.getContent().toByteArray());

        assertEquals(artistId.toString(), response.getArtist().getId());
        assertEquals(museumId.toString(), response.getMuseum().getId());
    }

    @Test
    void updateOnlyId() {
        final UUID paintingId = UUID.randomUUID();

        final String updateTitle = null;
        final String updateDescription = null;
        final byte[] updateContent = null;
        final String updateArtistId = null;
        final String updateMuseumId = null;

        final PaintingEntity updatedPainting = createPaintingEntity(
                paintingId,
                "Saved title",
                "Saved description",
                null,
                null,
                null
        );

        when(paintingDbService.update(
                paintingId.toString(),
                updateTitle,
                updateDescription,
                updateContent,
                updateArtistId,
                updateMuseumId
        )).thenReturn(updatedPainting);

        final UpdatePaintingRequest request = UpdatePaintingRequest.newBuilder()
                .setId(paintingId.toString())
                .build();

        grpcPaintingService.updatePainting(request, paintingResponseObserver);

        verify(paintingDbService).update(
                paintingId.toString(),
                updateTitle,
                updateDescription,
                updateContent,
                updateArtistId,
                updateMuseumId
        );
        verify(paintingResponseObserver).onNext(paintingResponseCaptor.capture());
        verify(paintingResponseObserver).onCompleted();

        final PaintingResponse response = paintingResponseCaptor.getValue();
        assertEquals(paintingId.toString(), response.getId());
        assertEquals("Saved title", response.getTitle());
        assertEquals("Saved description", response.getDescription());
        assertTrue(response.getContent().isEmpty());
        assertEquals(ArtistResponse.getDefaultInstance(), response.getArtist());
        assertEquals(MuseumResponse.getDefaultInstance(), response.getMuseum());
    }

    private PaintingEntity createPaintingEntity(final UUID id,
                                                final String title,
                                                final String description,
                                                final byte[] content,
                                                final ArtistEntity artist,
                                                final MuseumEntity museum) {
        final PaintingEntity painting = new PaintingEntity();
        painting.setId(id);
        painting.setTitle(title);
        painting.setDescription(description);
        painting.setContent(content);
        painting.setArtist(artist);
        painting.setMuseum(museum);
        return painting;
    }

    private ArtistEntity createArtistEntity(final UUID id,
                                            final String name,
                                            final String biography,
                                            final byte[] photo) {
        final ArtistEntity artist = new ArtistEntity();
        artist.setId(id);
        artist.setName(name);
        artist.setBiography(biography);
        artist.setPhoto(photo);
        return artist;
    }

    private MuseumEntity createMuseumEntity(final UUID id,
                                            final String title,
                                            final String description,
                                            final String city,
                                            final byte[] photo) {
        final MuseumEntity museum = new MuseumEntity();
        museum.setId(id);
        museum.setTitle(title);
        museum.setDescription(description);
        museum.setCity(city);
        museum.setPhoto(photo);
        return museum;
    }

    @Test
    void findPaintingsByName() {
        final UUID paintingId = UUID.randomUUID();
        final UUID artistId = UUID.randomUUID();
        final UUID museumId = UUID.randomUUID();

        final String title = "Mona Lisa";
        final int page = 0;
        final int size = 10;
        final long totalElements = 1L;
        final int totalPages = 1;

        final ArtistEntity artist = createArtistEntity(
                artistId,
                "Leonardo da Vinci",
                "Biography",
                "artist-photo".getBytes()
        );
        final MuseumEntity museum = createMuseumEntity(
                museumId,
                "Louvre",
                "Museum description",
                "Paris",
                "museum-photo".getBytes()
        );
        final PaintingEntity painting = createPaintingEntity(
                paintingId,
                title,
                "Description",
                "content".getBytes(),
                artist,
                museum
        );

        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<PaintingEntity> paintingsPage = new PageImpl<>(
                List.of(painting),
                pageRequest,
                totalElements
        );

        when(paintingDbService.getByTitle(title, pageRequest)).thenReturn(paintingsPage);

        final PaintingTitleRequest request = PaintingTitleRequest.newBuilder()
                .setTitle(title)
                .setPageable(PageableRequest.newBuilder()
                        .setPage(page)
                        .setSize(size)
                        .build())
                .build();

        grpcPaintingService.findPaintingsByName(request, paintingsResponseObserver);

        verify(paintingDbService).getByTitle(title, pageRequest);
        verify(paintingsResponseObserver).onNext(paintingsResponseCaptor.capture());
        verify(paintingsResponseObserver).onCompleted();

        final PaintingsResponse response = paintingsResponseCaptor.getValue();

        assertEquals(1, response.getPaintingsCount());
        assertEquals(paintingId.toString(), response.getPaintings(0).getId());
        assertEquals(title, response.getPaintings(0).getTitle());
        assertEquals("Description", response.getPaintings(0).getDescription());
        assertArrayEquals("content".getBytes(), response.getPaintings(0).getContent().toByteArray());

        assertEquals(artistId.toString(), response.getPaintings(0).getArtist().getId());
        assertEquals("Leonardo da Vinci", response.getPaintings(0).getArtist().getName());

        assertEquals(museumId.toString(), response.getPaintings(0).getMuseum().getId());
        assertEquals("Louvre", response.getPaintings(0).getMuseum().getTitle());

        assertEquals(page, response.getPage());
        assertEquals(size, response.getSize());
        assertEquals(totalElements, response.getTotalElements());
        assertEquals(totalPages, response.getTotalPages());
    }

    @Test
    void findPaintingsByNameShouldReturnEmptyPage() {
        final String title = "Unknown painting";
        final int page = 0;
        final int size = 10;

        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<PaintingEntity> paintingsPage = new PageImpl<>(
                List.of(),
                pageRequest,
                0
        );

        when(paintingDbService.getByTitle(title, pageRequest)).thenReturn(paintingsPage);

        final PaintingTitleRequest request = PaintingTitleRequest.newBuilder()
                .setTitle(title)
                .setPageable(PageableRequest.newBuilder()
                        .setPage(page)
                        .setSize(size)
                        .build())
                .build();

        grpcPaintingService.findPaintingsByName(request, paintingsResponseObserver);

        verify(paintingDbService).getByTitle(title, pageRequest);
        verify(paintingsResponseObserver).onNext(paintingsResponseCaptor.capture());
        verify(paintingsResponseObserver).onCompleted();

        final PaintingsResponse response = paintingsResponseCaptor.getValue();

        assertEquals(0, response.getPaintingsCount());
        assertEquals(page, response.getPage());
        assertEquals(size, response.getSize());
        assertEquals(0, response.getTotalElements());
        assertEquals(0, response.getTotalPages());
    }
}