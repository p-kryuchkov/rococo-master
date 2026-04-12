package io.student.rococo.service.db;

import io.student.rococo.data.entity.ArtistEntity;
import io.student.rococo.data.repository.ArtistRepository;
import io.student.rococo.exception.ArtistNotFoundException;
import io.student.rococo.exception.FieldValidationException;
import jakarta.persistence.EntityExistsException;
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
class ArtistDbServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private ArtistDbService artistDbService;

    @Test
    void shouldReturnArtistById() {
        final UUID id = UUID.randomUUID();
        final ArtistEntity artist = new ArtistEntity();
        artist.setId(id);
        artist.setName("Michelangelo");
        artist.setBiography("Biography");

        when(artistRepository.findById(id)).thenReturn(Optional.of(artist));

        ArtistEntity result = artistDbService.getById(id.toString());

        assertEquals(id, result.getId());
        assertEquals("Michelangelo", result.getName());
        assertEquals("Biography", result.getBiography());
        verify(artistRepository).findById(id);
    }

    @Test
    void shouldThrowWhenArtistNotFoundById() {
        UUID id = UUID.randomUUID();
        when(artistRepository.findById(id)).thenReturn(Optional.empty());

        ArtistNotFoundException exception = assertThrows(
                ArtistNotFoundException.class,
                () -> artistDbService.getById(id.toString())
        );

        assertEquals("Artist not found with id: " + id, exception.getMessage());
        verify(artistRepository).findById(id);
    }

    @Test
    void shouldReturnAllArtists() {
        final Pageable pageable = PageRequest.of(0, 10);
        final ArtistEntity first = new ArtistEntity();
        first.setName("Michelangelo");
        final ArtistEntity second = new ArtistEntity();
        second.setName("Donatello");
       final Page<ArtistEntity> page = new PageImpl<>(List.of(first, second));

        when(artistRepository.findAll(pageable)).thenReturn(page);

        Page<ArtistEntity> result = artistDbService.getAll(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("Michelangelo", result.getContent().get(0).getName());
        assertEquals("Donatello", result.getContent().get(1).getName());
        verify(artistRepository).findAll(pageable);
    }

    @Test
    void shouldCreateArtistWithPhoto() {
        final byte[] photo = {1, 2, 3};

        when(artistRepository.getByName("Michelangelo")).thenReturn(Optional.empty());
        when(artistRepository.save(any(ArtistEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArtistEntity result = artistDbService.create("Michelangelo", "Biography", photo);

        assertEquals("Michelangelo", result.getName());
        assertEquals("Biography", result.getBiography());
        assertArrayEquals(photo, result.getPhoto());

        verify(artistRepository).getByName("Michelangelo");
        verify(artistRepository).save(any(ArtistEntity.class));
    }

    @Test
    void shouldCreateArtistWithoutPhotoWhenPhotoIsNull() {
        when(artistRepository.getByName("Michelangelo")).thenReturn(Optional.empty());
        when(artistRepository.save(any(ArtistEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArtistEntity result = artistDbService.create("Michelangelo", "Biography", null);

        assertEquals("Michelangelo", result.getName());
        assertEquals("Biography", result.getBiography());
        assertNull(result.getPhoto());

        verify(artistRepository).getByName("Michelangelo");
        verify(artistRepository).save(any(ArtistEntity.class));
    }

    @Test
    void shouldCreateArtistWithoutPhotoWhenPhotoIsEmpty() {
        when(artistRepository.getByName("Michelangelo")).thenReturn(Optional.empty());
        when(artistRepository.save(any(ArtistEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArtistEntity result = artistDbService.create("Michelangelo", "Biography", new byte[0]);

        assertEquals("Michelangelo", result.getName());
        assertEquals("Biography", result.getBiography());
        assertNull(result.getPhoto());

        verify(artistRepository).getByName("Michelangelo");
        verify(artistRepository).save(any(ArtistEntity.class));
    }

    @Test
    void shouldThrowWhenCreateArtistWithDuplicateName() {
        final ArtistEntity existingArtist = new ArtistEntity();
        existingArtist.setId(UUID.randomUUID());
        existingArtist.setName("Michelangelo");

        when(artistRepository.getByName("Michelangelo")).thenReturn(Optional.of(existingArtist));

        EntityExistsException exception = assertThrows(
                EntityExistsException.class,
                () -> artistDbService.create("Michelangelo", "Biography", null)
        );

        assertEquals("Artist already exists with name: Michelangelo", exception.getMessage());
        verify(artistRepository).getByName("Michelangelo");
        verify(artistRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenCreateArtistWithBlankName() {
        FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> artistDbService.create("   ", "Biography", null)
        );

        assertEquals("Name must not be blank", exception.getMessage());
        verify(artistRepository, never()).getByName(any());
        verify(artistRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenCreateArtistWithBlankBiography() {
        FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> artistDbService.create("Michelangelo", "   ", null)
        );

        assertEquals("Biography must not be blank", exception.getMessage());
        verify(artistRepository, never()).getByName(any());
        verify(artistRepository, never()).save(any());
    }

    @Test
    void shouldUpdateAllFields() {
        final UUID id = UUID.randomUUID();
        final ArtistEntity existingArtist = new ArtistEntity();
        existingArtist.setId(id);
        existingArtist.setName("Old name");
        existingArtist.setBiography("Old biography");
        existingArtist.setPhoto(new byte[]{1,2,3});

        final byte[] newPhoto = {5, 6, 7};

        when(artistRepository.findById(id)).thenReturn(Optional.of(existingArtist));
        when(artistRepository.getByName("New name")).thenReturn(Optional.empty());
        when(artistRepository.save(any(ArtistEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArtistEntity result = artistDbService.update(
                id.toString(),
                "New name",
                "New biography",
                newPhoto
        );

        assertEquals("New name", result.getName());
        assertEquals("New biography", result.getBiography());
        assertArrayEquals(newPhoto, result.getPhoto());

        verify(artistRepository).findById(id);
        verify(artistRepository).getByName("New name");
        verify(artistRepository).save(existingArtist);
    }

    @Test
    void shouldThrowWhenUpdateArtistNotFound() {
        final UUID id = UUID.randomUUID();
        when(artistRepository.findById(id)).thenReturn(Optional.empty());

        ArtistNotFoundException exception = assertThrows(
                ArtistNotFoundException.class,
                () -> artistDbService.update(id.toString(), "Michelangelo", "Biography", null)
        );

        assertEquals("Artist not found with id: " + id, exception.getMessage());
        verify(artistRepository).findById(id);
        verify(artistRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenUpdateNameIsDuplicate() {
        final UUID firstId = UUID.randomUUID();
        final UUID secondId = UUID.randomUUID();

        final ArtistEntity existingArtist = new ArtistEntity();
        existingArtist.setId(firstId);
        existingArtist.setName("Old name");

        final ArtistEntity duplicateArtist = new ArtistEntity();
        duplicateArtist.setId(secondId);
        duplicateArtist.setName("New name");

        when(artistRepository.findById(firstId)).thenReturn(Optional.of(existingArtist));
        when(artistRepository.getByName("New name")).thenReturn(Optional.of(duplicateArtist));

        EntityExistsException exception = assertThrows(
                EntityExistsException.class,
                () -> artistDbService.update(firstId.toString(), "New name", null, null)
        );

        assertEquals("Artist already exists with name: New name", exception.getMessage());
        verify(artistRepository).findById(firstId);
        verify(artistRepository).getByName("New name");
        verify(artistRepository, never()).save(any());
    }

    @Test
    void shouldUpdateWhenNameBelongsToSameArtist() {
        final UUID id = UUID.randomUUID();

        final ArtistEntity existingArtist = new ArtistEntity();
        existingArtist.setId(id);
        existingArtist.setName("Michelangelo");
        existingArtist.setBiography("Old biography");

        final ArtistEntity sameArtist = new ArtistEntity();
        sameArtist.setId(id);
        sameArtist.setName("Michelangelo");

        when(artistRepository.findById(id)).thenReturn(Optional.of(existingArtist));
        when(artistRepository.getByName("Michelangelo")).thenReturn(Optional.of(sameArtist));
        when(artistRepository.save(any(ArtistEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArtistEntity result = artistDbService.update(id.toString(), "Michelangelo", "New biography", null);

        assertEquals("Michelangelo", result.getName());
        assertEquals("New biography", result.getBiography());

        verify(artistRepository).findById(id);
        verify(artistRepository).getByName("Michelangelo");
        verify(artistRepository).save(existingArtist);
    }

    @Test
    void shouldThrowWhenUpdateNameIsBlank() {
        final UUID id = UUID.randomUUID();
        final ArtistEntity existingArtist = new ArtistEntity();
        existingArtist.setId(id);

        when(artistRepository.findById(id)).thenReturn(Optional.of(existingArtist));

        FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> artistDbService.update(id.toString(), "   ", null, null)
        );

        assertEquals("Name must not be blank", exception.getMessage());
        verify(artistRepository).findById(id);
        verify(artistRepository, never()).getByName(any());
        verify(artistRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenUpdateBiographyIsBlank() {
       final UUID id = UUID.randomUUID();
        final ArtistEntity existingArtist = new ArtistEntity();
        existingArtist.setId(id);

        when(artistRepository.findById(id)).thenReturn(Optional.of(existingArtist));

        FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> artistDbService.update(id.toString(), null, "   ", null)
        );

        assertEquals("Biography must not be blank", exception.getMessage());
        verify(artistRepository).findById(id);
        verify(artistRepository, never()).save(any());
    }

    @Test
    void shouldClearPhotoWhenEmptyPhotoPassedToUpdate() {
        final UUID id = UUID.randomUUID();
        final ArtistEntity existingArtist = new ArtistEntity();
        existingArtist.setId(id);
        existingArtist.setPhoto(new byte[]{1, 2, 3});

        when(artistRepository.findById(id)).thenReturn(Optional.of(existingArtist));
        when(artistRepository.save(any(ArtistEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArtistEntity result = artistDbService.update(id.toString(), null, null, new byte[0]);

        assertNull(result.getPhoto());
        verify(artistRepository).findById(id);
        verify(artistRepository).save(existingArtist);
    }

    @Test
    void shouldNotChangePhotoWhenPhotoIsNullInUpdate() {
        final UUID id = UUID.randomUUID();
        final byte[] oldPhoto = {9, 8, 7};

        final ArtistEntity existingArtist = new ArtistEntity();
        existingArtist.setId(id);
        existingArtist.setName("Michelangelo");
        existingArtist.setBiography("Biography");
        existingArtist.setPhoto(oldPhoto);

        when(artistRepository.findById(id)).thenReturn(Optional.of(existingArtist));
        when(artistRepository.save(any(ArtistEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArtistEntity result = artistDbService.update(id.toString(), null, "Updated biography", null);

        assertEquals("Updated biography", result.getBiography());
        assertArrayEquals(oldPhoto, result.getPhoto());

        verify(artistRepository).findById(id);
        verify(artistRepository).save(existingArtist);
        verify(artistRepository, never()).getByName(any());
    }

    @Test
    void shouldThrowWhenGetArtistByInvalidId() {
        assertThrows(RuntimeException.class, () -> artistDbService.getById("abc"));
        verify(artistRepository, never()).findById(any());
    }

    @Test
    void shouldThrowWhenUpdateArtistByInvalidId() {
        assertThrows(RuntimeException.class, () -> artistDbService.update("abc", "Name", "Bio", null));
        verify(artistRepository, never()).findById(any());
    }

    @Test
    void shouldReturnArtistsByName() {
        final Pageable pageable = PageRequest.of(0, 10);

        final ArtistEntity first = new ArtistEntity();
        first.setId(UUID.randomUUID());
        first.setName("Michelangelo");
        first.setBiography("Biography 1");

        final ArtistEntity second = new ArtistEntity();
        second.setId(UUID.randomUUID());
        second.setName("angelo Bronzino");
        second.setBiography("Biography 2");

        final Page<ArtistEntity> page = new PageImpl<>(List.of(first, second), pageable, 2);

        when(artistRepository.findAllByNameContainingIgnoreCase("angelo", pageable)).thenReturn(page);

        Page<ArtistEntity> result = artistDbService.getByName("angelo", pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("Michelangelo", result.getContent().get(0).getName());
        assertEquals("angelo Bronzino", result.getContent().get(1).getName());

        verify(artistRepository).findAllByNameContainingIgnoreCase("angelo", pageable);
    }

    @Test
    void shouldTrimNameWhenGetArtistsByName() {
        final Pageable pageable = PageRequest.of(0, 10);
        final Page<ArtistEntity> page = new PageImpl<>(List.of(), pageable, 0);

        when(artistRepository.findAllByNameContainingIgnoreCase("Michelangelo", pageable)).thenReturn(page);

        Page<ArtistEntity> result = artistDbService.getByName("  Michelangelo  ", pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());

        verify(artistRepository).findAllByNameContainingIgnoreCase("Michelangelo", pageable);
    }

    @Test
    void shouldThrowWhenGetArtistsByNameIsBlank() {
        FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> artistDbService.getByName("   ", PageRequest.of(0, 10))
        );

        assertEquals("Name must not be blank", exception.getMessage());
        verify(artistRepository, never()).findAllByNameContainingIgnoreCase(any(), any());
    }

    @Test
    void shouldThrowWhenGetArtistsByNameIsNull() {
        FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> artistDbService.getByName(null, PageRequest.of(0, 10))
        );

        assertEquals("Name must not be blank", exception.getMessage());
        verify(artistRepository, never()).findAllByNameContainingIgnoreCase(any(), any());
    }
}