package io.student.rococo.service.db;

import io.student.rococo.data.entity.ArtistEntity;
import io.student.rococo.data.entity.MuseumEntity;
import io.student.rococo.data.entity.PaintingEntity;
import io.student.rococo.data.repository.ArtistRepository;
import io.student.rococo.data.repository.MuseumRepository;
import io.student.rococo.data.repository.PaintingRepository;
import io.student.rococo.exception.ArtistNotFoundException;
import io.student.rococo.exception.FieldValidationException;
import io.student.rococo.exception.MuseumNotFoundException;
import io.student.rococo.exception.PaintingNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaintingDbServiceTest {

    @Mock
    private PaintingRepository paintingRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private MuseumRepository museumRepository;

    @InjectMocks
    private PaintingDbService paintingDbService;

    @Test
    void shouldReturnPaintingById() {
        final UUID id = UUID.randomUUID();
        final PaintingEntity painting = new PaintingEntity();
        painting.setId(id);
        painting.setTitle("Mona Lisa");
        painting.setDescription("Painting description");

        when(paintingRepository.findById(id)).thenReturn(Optional.of(painting));

        PaintingEntity result = paintingDbService.getById(id.toString());

        assertEquals(id, result.getId());
        assertEquals("Mona Lisa", result.getTitle());
        assertEquals("Painting description", result.getDescription());
        verify(paintingRepository).findById(id);
    }

    @Test
    void shouldThrowWhenPaintingNotFoundById() {
        final UUID id = UUID.randomUUID();
        when(paintingRepository.findById(id)).thenReturn(Optional.empty());

        PaintingNotFoundException exception = assertThrows(
                PaintingNotFoundException.class,
                () -> paintingDbService.getById(id.toString())
        );

        assertEquals("Painting not found with id: " + id, exception.getMessage());
        verify(paintingRepository).findById(id);
    }

    @Test
    void shouldReturnAllPaintings() {
        final Pageable pageable = PageRequest.of(0, 10);

        final PaintingEntity firstPainting = new PaintingEntity();
        firstPainting.setTitle("Mona Lisa");

        final PaintingEntity secondPainting = new PaintingEntity();
        secondPainting.setTitle("The Starry Night");

        final Page<PaintingEntity> page = new PageImpl<>(List.of(firstPainting, secondPainting));

        when(paintingRepository.findAll(pageable)).thenReturn(page);

        Page<PaintingEntity> result = paintingDbService.getAll(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("Mona Lisa", result.getContent().get(0).getTitle());
        assertEquals("The Starry Night", result.getContent().get(1).getTitle());
        verify(paintingRepository).findAll(pageable);
    }

    @Test
    void shouldReturnPaintingsByArtistId() {
        final UUID artistId = UUID.randomUUID();
        final Pageable pageable = PageRequest.of(0, 10);

        final PaintingEntity firstPainting = new PaintingEntity();
        firstPainting.setTitle("Mona Lisa");

        final PaintingEntity secondPainting = new PaintingEntity();
        secondPainting.setTitle("The Starry Night\"");

        final Page<PaintingEntity> page = new PageImpl<>(List.of(firstPainting, secondPainting));

        when(paintingRepository.findByArtist_Id(artistId, pageable)).thenReturn(page);

        Page<PaintingEntity> result = paintingDbService.getByArtistId(artistId.toString(), pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("Mona Lisa", result.getContent().get(0).getTitle());
        assertEquals("The Starry Night\"", result.getContent().get(1).getTitle());
        verify(paintingRepository).findByArtist_Id(artistId, pageable);
    }

    @Test
    void shouldCreatePaintingWithContentAndMuseum() {
        final UUID artistId = UUID.randomUUID();
        final UUID museumId = UUID.randomUUID();
        final byte[] content = {1, 2, 3};

        final ArtistEntity artist = new ArtistEntity();
        artist.setId(artistId);
        artist.setName("Leonardo da Vinci");

        final MuseumEntity museum = new MuseumEntity();
        museum.setId(museumId);
        museum.setTitle("Louvre");

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));
        when(museumRepository.findById(museumId)).thenReturn(Optional.of(museum));
        when(paintingRepository.save(any(PaintingEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PaintingEntity result = paintingDbService.create(
                "Mona Lisa",
                "Painting description",
                content,
                artistId.toString(),
                museumId.toString()
        );

        assertEquals("Mona Lisa", result.getTitle());
        assertEquals("Painting description", result.getDescription());
        assertEquals(artist, result.getArtist());
        assertEquals(museum, result.getMuseum());
        assertArrayEquals(content, result.getContent());

        verify(artistRepository).findById(artistId);
        verify(museumRepository).findById(museumId);
        verify(paintingRepository).save(any(PaintingEntity.class));
    }

    @Test
    void shouldCreatePaintingWithoutContentAndMuseum() {
        final UUID artistId = UUID.randomUUID();

        final ArtistEntity artist = new ArtistEntity();
        artist.setId(artistId);
        artist.setName("Leonardo da Vinci");

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));
        when(paintingRepository.save(any(PaintingEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PaintingEntity result = paintingDbService.create(
                "Mona Lisa",
                "Painting description",
                null,
                artistId.toString(),
                null
        );

        assertEquals("Mona Lisa", result.getTitle());
        assertEquals("Painting description", result.getDescription());
        assertEquals(artist, result.getArtist());
        assertNull(result.getMuseum());
        assertNull(result.getContent());

        verify(artistRepository).findById(artistId);
        verify(museumRepository, never()).findById(any());
        verify(paintingRepository).save(any(PaintingEntity.class));
    }

    @Test
    void shouldCreatePaintingWithoutMuseumWhenMuseumIdIsBlank() {
        final UUID artistId = UUID.randomUUID();

        final ArtistEntity artist = new ArtistEntity();
        artist.setId(artistId);
        artist.setName("Leonardo da Vinci");

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));
        when(paintingRepository.save(any(PaintingEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PaintingEntity result = paintingDbService.create(
                "Mona Lisa",
                "Painting description",
                null,
                artistId.toString(),
                "   "
        );

        assertEquals("Mona Lisa", result.getTitle());
        assertEquals(artist, result.getArtist());
        assertNull(result.getMuseum());

        verify(artistRepository).findById(artistId);
        verify(museumRepository, never()).findById(any());
        verify(paintingRepository).save(any(PaintingEntity.class));
    }

    @Test
    void shouldThrowWhenCreatePaintingWithBlankTitle() {
        FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> paintingDbService.create("   ", "Painting description", null, UUID.randomUUID().toString(), null)
        );

        assertEquals("Title must not be blank", exception.getMessage());
        verify(artistRepository, never()).findById(any());
        verify(museumRepository, never()).findById(any());
        verify(paintingRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenCreatePaintingWithArtistNotFound() {
        final UUID artistId = UUID.randomUUID();

        when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

        ArtistNotFoundException exception = assertThrows(
                ArtistNotFoundException.class,
                () -> paintingDbService.create("Mona Lisa", "Painting description", null, artistId.toString(), null)
        );

        assertEquals("Artist not found by id: " + artistId, exception.getMessage());
        verify(artistRepository).findById(artistId);
        verify(museumRepository, never()).findById(any());
        verify(paintingRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenCreatePaintingWithMuseumNotFound() {
        final UUID artistId = UUID.randomUUID();
        final UUID museumId = UUID.randomUUID();

        final ArtistEntity artist = new ArtistEntity();
        artist.setId(artistId);
        artist.setName("Leonardo da Vinci");

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));
        when(museumRepository.findById(museumId)).thenReturn(Optional.empty());

        MuseumNotFoundException exception = assertThrows(
                MuseumNotFoundException.class,
                () -> paintingDbService.create(
                        "Mona Lisa",
                        "Painting description",
                        null,
                        artistId.toString(),
                        museumId.toString()
                )
        );

        assertEquals("Museum not found by id: " + museumId, exception.getMessage());
        verify(artistRepository).findById(artistId);
        verify(museumRepository).findById(museumId);
        verify(paintingRepository, never()).save(any());
    }

    @Test
    void shouldUpdateAllFields() {
        final UUID paintingId = UUID.randomUUID();
        final UUID artistId = UUID.randomUUID();
        final UUID museumId = UUID.randomUUID();

        final PaintingEntity existingPainting = new PaintingEntity();
        existingPainting.setId(paintingId);
        existingPainting.setTitle("Old title");
        existingPainting.setDescription("Old description");
        existingPainting.setContent(new byte[]{1, 2, 3});

        final ArtistEntity artist = new ArtistEntity();
        artist.setId(artistId);
        artist.setName("Vincent van Gogh");

        final MuseumEntity museum = new MuseumEntity();
        museum.setId(museumId);
        museum.setTitle("Prado");

        final byte[] newContent = {5, 6, 7};

        when(paintingRepository.findById(paintingId)).thenReturn(Optional.of(existingPainting));
        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));
        when(museumRepository.findById(museumId)).thenReturn(Optional.of(museum));
        when(paintingRepository.save(any(PaintingEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PaintingEntity result = paintingDbService.update(
                paintingId.toString(),
                "New title",
                "New description",
                newContent,
                artistId.toString(),
                museumId.toString()
        );

        assertEquals("New title", result.getTitle());
        assertEquals("New description", result.getDescription());
        assertArrayEquals(newContent, result.getContent());
        assertEquals(artist, result.getArtist());
        assertEquals(museum, result.getMuseum());

        verify(paintingRepository).findById(paintingId);
        verify(artistRepository).findById(artistId);
        verify(museumRepository).findById(museumId);
        verify(paintingRepository).save(existingPainting);
    }

    @Test
    void shouldThrowWhenUpdatePaintingNotFound() {
        final UUID paintingId = UUID.randomUUID();
        when(paintingRepository.findById(paintingId)).thenReturn(Optional.empty());

        PaintingNotFoundException exception = assertThrows(
                PaintingNotFoundException.class,
                () -> paintingDbService.update(paintingId.toString(), "Title", "Description", null, null, null)
        );

        assertEquals("Painting not found with id: " + paintingId, exception.getMessage());
        verify(paintingRepository).findById(paintingId);
        verify(paintingRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenUpdateTitleIsBlank() {
        final UUID paintingId = UUID.randomUUID();
        final PaintingEntity existingPainting = new PaintingEntity();
        existingPainting.setId(paintingId);

        when(paintingRepository.findById(paintingId)).thenReturn(Optional.of(existingPainting));

        FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> paintingDbService.update(paintingId.toString(), "   ", null, null, null, null)
        );

        assertEquals("Title must not be blank", exception.getMessage());
        verify(paintingRepository).findById(paintingId);
        verify(paintingRepository, never()).save(any());
    }

    @Test
    void shouldClearContentWhenEmptyContentPassedToUpdate() {
        final UUID paintingId = UUID.randomUUID();

        final PaintingEntity existingPainting = new PaintingEntity();
        existingPainting.setId(paintingId);
        existingPainting.setContent(new byte[]{1, 2, 3});

        when(paintingRepository.findById(paintingId)).thenReturn(Optional.of(existingPainting));
        when(paintingRepository.save(any(PaintingEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PaintingEntity result = paintingDbService.update(
                paintingId.toString(),
                null,
                null,
                new byte[0],
                null,
                null
        );

        assertNull(result.getContent());
        verify(paintingRepository).findById(paintingId);
        verify(paintingRepository).save(existingPainting);
    }

    @Test
    void shouldNotChangeContentWhenContentIsNullInUpdate() {
        final UUID paintingId = UUID.randomUUID();
        final byte[] oldContent = {9, 8, 7};

        final PaintingEntity existingPainting = new PaintingEntity();
        existingPainting.setId(paintingId);
        existingPainting.setTitle("Mona Lisa");
        existingPainting.setDescription("Old description");
        existingPainting.setContent(oldContent);

        when(paintingRepository.findById(paintingId)).thenReturn(Optional.of(existingPainting));
        when(paintingRepository.save(any(PaintingEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PaintingEntity result = paintingDbService.update(
                paintingId.toString(),
                null,
                "Updated description",
                null,
                null,
                null
        );

        assertEquals("Updated description", result.getDescription());
        assertArrayEquals(oldContent, result.getContent());

        verify(paintingRepository).findById(paintingId);
        verify(paintingRepository).save(existingPainting);
        verify(artistRepository, never()).findById(any());
        verify(museumRepository, never()).findById(any());
    }

    @Test
    void shouldThrowWhenUpdateArtistNotFound() {
        final UUID paintingId = UUID.randomUUID();
        final UUID artistId = UUID.randomUUID();

        final PaintingEntity existingPainting = new PaintingEntity();
        existingPainting.setId(paintingId);

        when(paintingRepository.findById(paintingId)).thenReturn(Optional.of(existingPainting));
        when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

        ArtistNotFoundException exception = assertThrows(
                ArtistNotFoundException.class,
                () -> paintingDbService.update(paintingId.toString(), null, null, null, artistId.toString(), null)
        );

        assertEquals("Artist not found by id: " + artistId, exception.getMessage());
        verify(paintingRepository).findById(paintingId);
        verify(artistRepository).findById(artistId);
        verify(paintingRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenUpdateMuseumNotFound() {
        final UUID paintingId = UUID.randomUUID();
        final UUID museumId = UUID.randomUUID();

        final PaintingEntity existingPainting = new PaintingEntity();
        existingPainting.setId(paintingId);

        when(paintingRepository.findById(paintingId)).thenReturn(Optional.of(existingPainting));
        when(museumRepository.findById(museumId)).thenReturn(Optional.empty());

        MuseumNotFoundException exception = assertThrows(
                MuseumNotFoundException.class,
                () -> paintingDbService.update(paintingId.toString(), null, null, null, null, museumId.toString())
        );

        assertEquals("Museum not found by id: " + museumId, exception.getMessage());
        verify(paintingRepository).findById(paintingId);
        verify(museumRepository).findById(museumId);
        verify(paintingRepository, never()).save(any());
    }

    @Test
    void shouldClearMuseumWhenMuseumIdIsBlankInUpdate() {
        final UUID paintingId = UUID.randomUUID();

        final MuseumEntity oldMuseum = new MuseumEntity();
        oldMuseum.setId(UUID.randomUUID());
        oldMuseum.setTitle("Old museum");

        final PaintingEntity existingPainting = new PaintingEntity();
        existingPainting.setId(paintingId);
        existingPainting.setMuseum(oldMuseum);

        when(paintingRepository.findById(paintingId)).thenReturn(Optional.of(existingPainting));
        when(paintingRepository.save(any(PaintingEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PaintingEntity result = paintingDbService.update(
                paintingId.toString(),
                null,
                null,
                null,
                null,
                "   "
        );

        assertNull(result.getMuseum());
        verify(paintingRepository).findById(paintingId);
        verify(museumRepository, never()).findById(any());
        verify(paintingRepository).save(existingPainting);
    }

    @Test
    void shouldReturnPaintingsByTitle() {
        final Pageable pageable = PageRequest.of(0, 10);
        final String title = "mona";

        final PaintingEntity painting = new PaintingEntity();
        painting.setTitle("Mona Lisa");

        final Page<PaintingEntity> page = new PageImpl<>(List.of(painting), pageable, 1);

        when(paintingRepository.findAllByTitleContainingIgnoreCase("mona", pageable))
                .thenReturn(page);

        Page<PaintingEntity> result = paintingDbService.getByTitle(title, pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("Mona Lisa", result.getContent().get(0).getTitle());

        verify(paintingRepository).findAllByTitleContainingIgnoreCase("mona", pageable);
    }

    @Test
    void shouldTrimTitleWhenFindPaintingsByTitle() {
        final Pageable pageable = PageRequest.of(0, 10);
        final String title = "  mona  ";

        final PaintingEntity painting = new PaintingEntity();
        painting.setTitle("Mona Lisa");

        final Page<PaintingEntity> page = new PageImpl<>(List.of(painting), pageable, 1);

        when(paintingRepository.findAllByTitleContainingIgnoreCase("mona", pageable))
                .thenReturn(page);

        Page<PaintingEntity> result = paintingDbService.getByTitle(title, pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("Mona Lisa", result.getContent().get(0).getTitle());

        verify(paintingRepository).findAllByTitleContainingIgnoreCase("mona", pageable);
    }

    @Test
    void shouldReturnEmptyPageWhenPaintingTitleNotFound() {
        final Pageable pageable = PageRequest.of(0, 10);
        final String title = "unknown";

        final Page<PaintingEntity> page = new PageImpl<>(List.of(), pageable, 0);

        when(paintingRepository.findAllByTitleContainingIgnoreCase(title, pageable))
                .thenReturn(page);

        Page<PaintingEntity> result = paintingDbService.getByTitle(title, pageable);

        assertTrue(result.getContent().isEmpty());

        verify(paintingRepository).findAllByTitleContainingIgnoreCase(title, pageable);
    }

    @Test
    void shouldThrowWhenFindPaintingsByTitleWithNullTitle() {
        FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> paintingDbService.getByTitle(null, PageRequest.of(0, 10))
        );

        assertEquals("Title must not be blank", exception.getMessage());

        verify(paintingRepository, never()).findAllByTitleContainingIgnoreCase(any(), any());
    }

    @Test
    void shouldThrowWhenFindPaintingsByTitleWithBlankTitle() {
        FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> paintingDbService.getByTitle("   ", PageRequest.of(0, 10))
        );

        assertEquals("Title must not be blank", exception.getMessage());

        verify(paintingRepository, never()).findAllByTitleContainingIgnoreCase(any(), any());
    }
}