package io.student.rococo.service.db;

import io.student.rococo.data.entity.CountryEntity;
import io.student.rococo.data.entity.MuseumEntity;
import io.student.rococo.data.repository.CountryRepository;
import io.student.rococo.data.repository.MuseumRepository;
import io.student.rococo.exception.CountryNotFoundException;
import io.student.rococo.exception.FieldValidationException;
import io.student.rococo.exception.MuseumNotFoundException;
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
class MuseumDbServiceTest {

    @Mock
    private MuseumRepository museumRepository;

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private MuseumDbService museumDbService;

    @Test
    void shouldReturnMuseumById() {
        final UUID id = UUID.randomUUID();
        final MuseumEntity museum = new MuseumEntity();
        museum.setId(id);
        museum.setTitle("Louvre");
        museum.setDescription("Museum description");
        museum.setCity("Paris");

        when(museumRepository.findById(id)).thenReturn(Optional.of(museum));

        MuseumEntity result = museumDbService.getById(id.toString());

        assertEquals(id, result.getId());
        assertEquals("Louvre", result.getTitle());
        assertEquals("Museum description", result.getDescription());
        assertEquals("Paris", result.getCity());
        verify(museumRepository).findById(id);
    }

    @Test
    void shouldThrowWhenMuseumNotFoundById() {
        final UUID id = UUID.randomUUID();
        when(museumRepository.findById(id)).thenReturn(Optional.empty());

        MuseumNotFoundException exception = assertThrows(
                MuseumNotFoundException.class,
                () -> museumDbService.getById(id.toString())
        );

        assertEquals("Museum not found with id: " + id, exception.getMessage());
        verify(museumRepository).findById(id);
    }

    @Test
    void shouldReturnAllMuseums() {
        final Pageable pageable = PageRequest.of(0, 10);

        final MuseumEntity firstMuseum = new MuseumEntity();
        firstMuseum.setTitle("Louvre");

        final MuseumEntity secondMuseum = new MuseumEntity();
        secondMuseum.setTitle("Ermitage");

        final Page<MuseumEntity> page = new PageImpl<>(List.of(firstMuseum, secondMuseum));

        when(museumRepository.findAll(pageable)).thenReturn(page);

        Page<MuseumEntity> result = museumDbService.getAll(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("Louvre", result.getContent().get(0).getTitle());
        assertEquals("Ermitage", result.getContent().get(1).getTitle());
        verify(museumRepository).findAll(pageable);
    }

    @Test
    void shouldCreateMuseumWithPhoto() {
        final UUID countryId = UUID.randomUUID();
        final byte[] photo = {1, 2, 3};

        final CountryEntity country = new CountryEntity();
        country.setId(countryId);
        country.setName("France");

        when(museumRepository.getByTitle("Louvre")).thenReturn(Optional.empty());
        when(countryRepository.findById(countryId)).thenReturn(Optional.of(country));
        when(museumRepository.save(any(MuseumEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MuseumEntity result = museumDbService.create(
                "Louvre",
                "Museum description",
                "Paris",
                countryId.toString(),
                photo
        );

        assertEquals("Louvre", result.getTitle());
        assertEquals("Museum description", result.getDescription());
        assertEquals("Paris", result.getCity());
        assertEquals(country, result.getCountry());
        assertArrayEquals(photo, result.getPhoto());

        verify(museumRepository).getByTitle("Louvre");
        verify(countryRepository).findById(countryId);
        verify(museumRepository).save(any(MuseumEntity.class));
    }

    @Test
    void shouldCreateMuseumWithoutPhotoWhenPhotoIsNull() {
        final UUID countryId = UUID.randomUUID();

        final CountryEntity country = new CountryEntity();
        country.setId(countryId);
        country.setName("France");

        when(museumRepository.getByTitle("Louvre")).thenReturn(Optional.empty());
        when(countryRepository.findById(countryId)).thenReturn(Optional.of(country));
        when(museumRepository.save(any(MuseumEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MuseumEntity result = museumDbService.create(
                "Louvre",
                "Museum description",
                "Paris",
                countryId.toString(),
                null
        );

        assertEquals("Louvre", result.getTitle());
        assertEquals("Museum description", result.getDescription());
        assertEquals("Paris", result.getCity());
        assertEquals(country, result.getCountry());
        assertNull(result.getPhoto());

        verify(museumRepository).getByTitle("Louvre");
        verify(countryRepository).findById(countryId);
        verify(museumRepository).save(any(MuseumEntity.class));
    }

    @Test
    void shouldCreateMuseumWithoutPhotoWhenPhotoIsEmpty() {
        final UUID countryId = UUID.randomUUID();

        final CountryEntity country = new CountryEntity();
        country.setId(countryId);
        country.setName("France");

        when(museumRepository.getByTitle("Louvre")).thenReturn(Optional.empty());
        when(countryRepository.findById(countryId)).thenReturn(Optional.of(country));
        when(museumRepository.save(any(MuseumEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MuseumEntity result = museumDbService.create(
                "Louvre",
                "Museum description",
                "Paris",
                countryId.toString(),
                new byte[0]
        );

        assertEquals("Louvre", result.getTitle());
        assertEquals("Museum description", result.getDescription());
        assertEquals("Paris", result.getCity());
        assertEquals(country, result.getCountry());
        assertNull(result.getPhoto());

        verify(museumRepository).getByTitle("Louvre");
        verify(countryRepository).findById(countryId);
        verify(museumRepository).save(any(MuseumEntity.class));
    }

    @Test
    void shouldThrowWhenCreateMuseumWithDuplicateTitle() {
        final MuseumEntity existingMuseum = new MuseumEntity();
        existingMuseum.setId(UUID.randomUUID());
        existingMuseum.setTitle("Louvre");

        when(museumRepository.getByTitle("Louvre")).thenReturn(Optional.of(existingMuseum));

        EntityExistsException exception = assertThrows(
                EntityExistsException.class,
                () -> museumDbService.create("Louvre", "Museum description", "Paris", UUID.randomUUID().toString(), null)
        );

        assertEquals("Museum already exists with title: Louvre", exception.getMessage());
        verify(museumRepository).getByTitle("Louvre");
        verify(countryRepository, never()).findById(any());
        verify(museumRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenCreateMuseumWithBlankTitle() {
        FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> museumDbService.create("   ", "Museum description", "Paris", UUID.randomUUID().toString(), null)
        );

        assertEquals("Title must not be blank", exception.getMessage());
        verify(museumRepository, never()).getByTitle(any());
        verify(countryRepository, never()).findById(any());
        verify(museumRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenCreateMuseumWithBlankCountryId() {
        FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> museumDbService.create("Louvre", "Museum description", "Paris", "   ", null)
        );

        assertEquals("Country Id must not be blank", exception.getMessage());
        verify(museumRepository, never()).getByTitle(any());
        verify(countryRepository, never()).findById(any());
        verify(museumRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenCreateMuseumWithCountryNotFound() {
        final UUID countryId = UUID.randomUUID();

        when(museumRepository.getByTitle("Louvre")).thenReturn(Optional.empty());
        when(countryRepository.findById(countryId)).thenReturn(Optional.empty());

        CountryNotFoundException exception = assertThrows(
                CountryNotFoundException.class,
                () -> museumDbService.create("Louvre", "Museum description", "Paris", countryId.toString(), null)
        );

        assertEquals("Country not found with id: " + countryId, exception.getMessage());
        verify(museumRepository).getByTitle("Louvre");
        verify(countryRepository).findById(countryId);
        verify(museumRepository, never()).save(any());
    }

    @Test
    void shouldUpdateAllFields() {
        final UUID museumId = UUID.randomUUID();
        final UUID countryId = UUID.randomUUID();

        final MuseumEntity existingMuseum = new MuseumEntity();
        existingMuseum.setId(museumId);
        existingMuseum.setTitle("Old title");
        existingMuseum.setDescription("Old description");
        existingMuseum.setCity("Old city");
        existingMuseum.setPhoto(new byte[]{1, 2, 3});

        final CountryEntity newCountry = new CountryEntity();
        newCountry.setId(countryId);
        newCountry.setName("Spain");

        final byte[] newPhoto = {5, 6, 7};

        when(museumRepository.findById(museumId)).thenReturn(Optional.of(existingMuseum));
        when(museumRepository.getByTitle("New title")).thenReturn(Optional.empty());
        when(countryRepository.findById(countryId)).thenReturn(Optional.of(newCountry));
        when(museumRepository.save(any(MuseumEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MuseumEntity result = museumDbService.update(
                museumId.toString(),
                "New title",
                "New description",
                "New city",
                countryId.toString(),
                newPhoto
        );

        assertEquals("New title", result.getTitle());
        assertEquals("New description", result.getDescription());
        assertEquals("New city", result.getCity());
        assertEquals(newCountry, result.getCountry());
        assertArrayEquals(newPhoto, result.getPhoto());

        verify(museumRepository).findById(museumId);
        verify(museumRepository).getByTitle("New title");
        verify(countryRepository).findById(countryId);
        verify(museumRepository).save(existingMuseum);
    }

    @Test
    void shouldThrowWhenUpdateMuseumNotFound() {
        final UUID museumId = UUID.randomUUID();
        when(museumRepository.findById(museumId)).thenReturn(Optional.empty());

        MuseumNotFoundException exception = assertThrows(
                MuseumNotFoundException.class,
                () -> museumDbService.update(museumId.toString(), "Title", "Description", "City", null, null)
        );

        assertEquals("Museum not found with id: " + museumId, exception.getMessage());
        verify(museumRepository).findById(museumId);
        verify(museumRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenUpdateTitleIsDuplicate() {
        final UUID firstId = UUID.randomUUID();
        final UUID secondId = UUID.randomUUID();

        final MuseumEntity existingMuseum = new MuseumEntity();
        existingMuseum.setId(firstId);
        existingMuseum.setTitle("Old title");

        final MuseumEntity duplicateMuseum = new MuseumEntity();
        duplicateMuseum.setId(secondId);
        duplicateMuseum.setTitle("New title");

        when(museumRepository.findById(firstId)).thenReturn(Optional.of(existingMuseum));
        when(museumRepository.getByTitle("New title")).thenReturn(Optional.of(duplicateMuseum));

        EntityExistsException exception = assertThrows(
                EntityExistsException.class,
                () -> museumDbService.update(firstId.toString(), "New title", null, null, null, null)
        );

        assertEquals("Museum already exists with title: New title", exception.getMessage());
        verify(museumRepository).findById(firstId);
        verify(museumRepository).getByTitle("New title");
        verify(museumRepository, never()).save(any());
    }

    @Test
    void shouldAllowUpdateWhenTitleBelongsToSameMuseum() {
        final UUID id = UUID.randomUUID();

        final MuseumEntity existingMuseum = new MuseumEntity();
        existingMuseum.setId(id);
        existingMuseum.setTitle("Louvre");
        existingMuseum.setDescription("Old description");

        final MuseumEntity sameMuseum = new MuseumEntity();
        sameMuseum.setId(id);
        sameMuseum.setTitle("Louvre");

        when(museumRepository.findById(id)).thenReturn(Optional.of(existingMuseum));
        when(museumRepository.getByTitle("Louvre")).thenReturn(Optional.of(sameMuseum));
        when(museumRepository.save(any(MuseumEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MuseumEntity result = museumDbService.update(id.toString(), "Louvre", "New description", null, null, null);

        assertEquals("Louvre", result.getTitle());
        assertEquals("New description", result.getDescription());

        verify(museumRepository).findById(id);
        verify(museumRepository).getByTitle("Louvre");
        verify(museumRepository).save(existingMuseum);
    }

    @Test
    void shouldThrowWhenUpdateTitleIsBlank() {
        final UUID id = UUID.randomUUID();
        final MuseumEntity existingMuseum = new MuseumEntity();
        existingMuseum.setId(id);

        when(museumRepository.findById(id)).thenReturn(Optional.of(existingMuseum));

        FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> museumDbService.update(id.toString(), "   ", null, null, null, null)
        );

        assertEquals("Title must not be blank", exception.getMessage());
        verify(museumRepository).findById(id);
        verify(museumRepository, never()).getByTitle(any());
        verify(museumRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenUpdateCountryNotFound() {
        final UUID museumId = UUID.randomUUID();
        final UUID countryId = UUID.randomUUID();

        final MuseumEntity existingMuseum = new MuseumEntity();
        existingMuseum.setId(museumId);

        when(museumRepository.findById(museumId)).thenReturn(Optional.of(existingMuseum));
        when(countryRepository.findById(countryId)).thenReturn(Optional.empty());

        CountryNotFoundException exception = assertThrows(
                CountryNotFoundException.class,
                () -> museumDbService.update(museumId.toString(), null, null, null, countryId.toString(), null)
        );

        assertEquals("Country not found with id: " + countryId, exception.getMessage());
        verify(museumRepository).findById(museumId);
        verify(countryRepository).findById(countryId);
        verify(museumRepository, never()).save(any());
    }

    @Test
    void shouldClearPhotoWhenEmptyPhotoPassedToUpdate() {
        final UUID museumId = UUID.randomUUID();
        final MuseumEntity existingMuseum = new MuseumEntity();
        existingMuseum.setId(museumId);
        existingMuseum.setPhoto(new byte[]{1, 2, 3});

        when(museumRepository.findById(museumId)).thenReturn(Optional.of(existingMuseum));
        when(museumRepository.save(any(MuseumEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MuseumEntity result = museumDbService.update(museumId.toString(), null, null, null, null, new byte[0]);

        assertNull(result.getPhoto());
        verify(museumRepository).findById(museumId);
        verify(museumRepository).save(existingMuseum);
    }

    @Test
    void shouldNotChangePhotoWhenPhotoIsNullInUpdate() {
        final UUID museumId = UUID.randomUUID();
        final byte[] oldPhoto = {9, 8, 7};

        final MuseumEntity existingMuseum = new MuseumEntity();
        existingMuseum.setId(museumId);
        existingMuseum.setTitle("Louvre");
        existingMuseum.setDescription("Description");
        existingMuseum.setCity("Paris");
        existingMuseum.setPhoto(oldPhoto);

        when(museumRepository.findById(museumId)).thenReturn(Optional.of(existingMuseum));
        when(museumRepository.save(any(MuseumEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MuseumEntity result = museumDbService.update(
                museumId.toString(),
                null,
                "Updated description",
                null,
                null,
                null
        );

        assertEquals("Updated description", result.getDescription());
        assertArrayEquals(oldPhoto, result.getPhoto());

        verify(museumRepository).findById(museumId);
        verify(museumRepository).save(existingMuseum);
        verify(museumRepository, never()).getByTitle(any());
    }
}