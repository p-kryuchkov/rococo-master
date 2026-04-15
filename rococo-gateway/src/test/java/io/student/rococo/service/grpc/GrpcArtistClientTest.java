package io.student.rococo.service.grpc;

import com.google.protobuf.ByteString;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.student.rococo.exception.GrpcStatusException;
import io.student.rococo.grpc.*;
import io.student.rococo.model.ArtistJson;
import io.student.rococo.model.EventJson;
import io.student.rococo.utils.CurrentUserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import static io.student.rococo.model.EventType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GrpcArtistClientTest {

    private final UUID leonardoId = UUID.randomUUID();
    private final UUID raphaelId = UUID.randomUUID();

    private final String username = "splinter";
    private final String leonardoName = "Leonardo";
    private final String leonardoBiography = "Leonardo biography";

    private final KafkaTemplate<String, EventJson> kafkaTemplate = mock(KafkaTemplate.class);
    private final CurrentUserProvider currentUserProvider = mock(CurrentUserProvider.class);
    private final ArtistServiceGrpc.ArtistServiceBlockingStub stub =
            mock(ArtistServiceGrpc.ArtistServiceBlockingStub.class);

    private final GrpcArtistClient client = new GrpcArtistClient(kafkaTemplate, currentUserProvider);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(client, "stub", stub);
        when(currentUserProvider.getUsername()).thenReturn(username);
    }

    @Test
    void getArtistById() {
        final ArtistResponse response = ArtistResponse.newBuilder()
                .setId(leonardoId.toString())
                .setName(leonardoName)
                .setBiography(leonardoBiography)
                .build();

        when(stub.getArtistById(any(IdRequest.class))).thenReturn(response);

        ArtistJson result = client.getArtistById(leonardoId);

        assertNotNull(result);
        assertEquals(leonardoId, result.id());
        assertEquals(leonardoName, result.name());
        assertEquals(leonardoBiography, result.biography());

        ArgumentCaptor<IdRequest> requestCaptor = ArgumentCaptor.forClass(IdRequest.class);
        verify(stub).getArtistById(requestCaptor.capture());
        assertEquals(leonardoId.toString(), requestCaptor.getValue().getId());

        ArgumentCaptor<EventJson> eventCaptor = ArgumentCaptor.forClass(EventJson.class);
        verify(kafkaTemplate).send(eq("events"), eventCaptor.capture());

        EventJson event = eventCaptor.getValue();
        assertNotNull(event.date());
        assertEquals(GET, event.eventType());
        assertEquals("Get artist by ID", event.description());
        assertEquals(leonardoId, event.entityId());
        assertEquals(username, event.username());
    }

    @Test
    void getArtistByIdShouldThrowGrpcStatusException() {
        when(stub.getArtistById(any(IdRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.NOT_FOUND));

        assertThrows(GrpcStatusException.class, () -> client.getArtistById(leonardoId));

        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void getArtistByIdShouldThrowBadRequestWhenIdIsNull() {
        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> client.getArtistById(null));

        assertEquals(400, exception.getStatusCode().value());
        assertEquals("Artist id is required", exception.getReason());

        verifyNoInteractions(stub);
        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void getAllArtists() {
        final ArtistResponse leonardo = ArtistResponse.newBuilder()
                .setId(leonardoId.toString())
                .setName(leonardoName)
                .setBiography(leonardoBiography)
                .build();

        String raphaelName = "Raphael";
        String raphaelBiography = "Raphael biography";
        final ArtistResponse raphael = ArtistResponse.newBuilder()
                .setId(raphaelId.toString())
                .setName(raphaelName)
                .setBiography(raphaelBiography)
                .build();

        final ArtistsResponse response = ArtistsResponse.newBuilder()
                .addArtists(leonardo)
                .addArtists(raphael)
                .setTotalElements(5)
                .build();

        when(stub.allArtists(any(PageableRequest.class))).thenReturn(response);

        PageRequest pageable = PageRequest.of(1, 2);

        Page<ArtistJson> result = client.getAllArtists(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(5, result.getTotalElements());
        assertEquals(leonardoName, result.getContent().get(0).name());
        assertEquals(raphaelName, result.getContent().get(1).name());

        ArgumentCaptor<PageableRequest> requestCaptor = ArgumentCaptor.forClass(PageableRequest.class);
        verify(stub).allArtists(requestCaptor.capture());
        assertEquals(1, requestCaptor.getValue().getPage());
        assertEquals(2, requestCaptor.getValue().getSize());

        ArgumentCaptor<EventJson> eventCaptor = ArgumentCaptor.forClass(EventJson.class);
        verify(kafkaTemplate).send(eq("events"), eventCaptor.capture());

        EventJson event = eventCaptor.getValue();
        assertNotNull(event.date());
        assertEquals(GET, event.eventType());
        assertEquals("Get all artists", event.description());
        assertNull(event.entityId());
        assertEquals(username, event.username());
    }

    @Test
    void getAllArtistsShouldThrowGrpcStatusException() {
        when(stub.allArtists(any(PageableRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        PageRequest pageable = PageRequest.of(0, 20);

        assertThrows(GrpcStatusException.class, () -> client.getAllArtists(pageable));

        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void createArtistWithPhoto() {
        ArtistJson artist = mock(ArtistJson.class);
        when(artist.name()).thenReturn(leonardoName);
        when(artist.biography()).thenReturn(leonardoBiography);

        byte[] photoBytes = "leonardo-photo".getBytes(StandardCharsets.UTF_8);
        String base64Photo = "data:image/png;base64," + Base64.getEncoder().encodeToString(photoBytes);
        when(artist.photo()).thenReturn(base64Photo);

        final ArtistResponse response = ArtistResponse.newBuilder()
                .setId(leonardoId.toString())
                .setName(leonardoName)
                .setBiography(leonardoBiography)
                .build();

        when(stub.createArtist(any(CreateArtistRequest.class))).thenReturn(response);

        ArtistJson result = client.createArtist(artist);

        assertNotNull(result);
        assertEquals(leonardoId, result.id());
        assertEquals(leonardoName, result.name());
        assertEquals(leonardoBiography, result.biography());

        ArgumentCaptor<CreateArtistRequest> requestCaptor = ArgumentCaptor.forClass(CreateArtistRequest.class);
        verify(stub).createArtist(requestCaptor.capture());

        CreateArtistRequest request = requestCaptor.getValue();
        assertEquals(leonardoName, request.getName());
        assertEquals(leonardoBiography, request.getBiography());
        assertEquals(ByteString.copyFrom(photoBytes), request.getPhoto());

        ArgumentCaptor<EventJson> eventCaptor = ArgumentCaptor.forClass(EventJson.class);
        verify(kafkaTemplate).send(eq("events"), eventCaptor.capture());

        EventJson event = eventCaptor.getValue();
        assertNotNull(event.date());
        assertEquals(CREATE, event.eventType());
        assertEquals("Create artist", event.description());
        assertEquals(leonardoId, event.entityId());
        assertEquals(username, event.username());
    }

    @Test
    void createArtistWithoutPhoto() {
        ArtistJson artist = mock(ArtistJson.class);
        when(artist.name()).thenReturn(leonardoName);
        when(artist.biography()).thenReturn(leonardoBiography);
        when(artist.photo()).thenReturn(null);

        final ArtistResponse response = ArtistResponse.newBuilder()
                .setId(leonardoId.toString())
                .setName(leonardoName)
                .setBiography(leonardoBiography)
                .build();

        when(stub.createArtist(any(CreateArtistRequest.class))).thenReturn(response);

        client.createArtist(artist);

        ArgumentCaptor<CreateArtistRequest> requestCaptor = ArgumentCaptor.forClass(CreateArtistRequest.class);
        verify(stub).createArtist(requestCaptor.capture());

        CreateArtistRequest request = requestCaptor.getValue();
        assertEquals(leonardoName, request.getName());
        assertEquals(leonardoBiography, request.getBiography());
        assertTrue(request.getPhoto().isEmpty());
    }

    @Test
    void updateArtistShouldThrowBadRequestWhenIdIsNull() {
        ArtistJson artist = mock(ArtistJson.class);
        when(artist.id()).thenReturn(null);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> client.updateArtist(artist));

        assertEquals(400, exception.getStatusCode().value());
        assertEquals("Artist id is required", exception.getReason());

        verifyNoInteractions(stub);
        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void updateArtist() {
        ArtistJson artist = mock(ArtistJson.class);
        when(artist.id()).thenReturn(leonardoId);
        when(artist.name()).thenReturn(leonardoName);
        String updatedBiography = "Updated biography";
        when(artist.biography()).thenReturn(updatedBiography);

        byte[] photoBytes = "updated-leonardo-photo".getBytes(StandardCharsets.UTF_8);
        String base64Photo = "data:image/png;base64," + Base64.getEncoder().encodeToString(photoBytes);
        when(artist.photo()).thenReturn(base64Photo);

        final ArtistResponse response = ArtistResponse.newBuilder()
                .setId(leonardoId.toString())
                .setName(leonardoName)
                .setBiography(updatedBiography)
                .build();

        when(stub.updateArtist(any(UpdateArtistRequest.class))).thenReturn(response);

        ArtistJson result = client.updateArtist(artist);

        assertNotNull(result);
        assertEquals(leonardoId, result.id());
        assertEquals(leonardoName, result.name());
        assertEquals(updatedBiography, result.biography());

        ArgumentCaptor<UpdateArtistRequest> requestCaptor = ArgumentCaptor.forClass(UpdateArtistRequest.class);
        verify(stub).updateArtist(requestCaptor.capture());

        UpdateArtistRequest request = requestCaptor.getValue();
        assertEquals(leonardoId.toString(), request.getId());
        assertEquals(leonardoName, request.getName());
        assertEquals(updatedBiography, request.getBiography());
        assertEquals(ByteString.copyFrom(photoBytes), request.getPhoto());

        ArgumentCaptor<EventJson> eventCaptor = ArgumentCaptor.forClass(EventJson.class);
        verify(kafkaTemplate).send(eq("events"), eventCaptor.capture());

        EventJson event = eventCaptor.getValue();
        assertNotNull(event.date());
        assertEquals(UPDATE, event.eventType());
        assertEquals("Update artist", event.description());
        assertEquals(leonardoId, event.entityId());
        assertEquals(username, event.username());
    }

    @Test
    void updateArtistShouldThrowGrpcStatusException() {
        ArtistJson artist = mock(ArtistJson.class);
        when(artist.id()).thenReturn(leonardoId);
        when(artist.name()).thenReturn(leonardoName);
        when(artist.biography()).thenReturn(leonardoBiography);
        when(artist.photo()).thenReturn(null);

        when(stub.updateArtist(any(UpdateArtistRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        assertThrows(GrpcStatusException.class, () -> client.updateArtist(artist));

        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void getArtistsByName() {
        final ArtistResponse leonardo = ArtistResponse.newBuilder()
                .setId(leonardoId.toString())
                .setName(leonardoName)
                .setBiography(leonardoBiography)
                .build();

        final ArtistsResponse response = ArtistsResponse.newBuilder()
                .addArtists(leonardo)
                .setTotalElements(1)
                .build();

        when(stub.getArtistsByName(any(ArtistNameRequest.class))).thenReturn(response);

        PageRequest pageable = PageRequest.of(0, 10);

        Page<ArtistJson> result = client.getArtistsByName(leonardoName, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        assertEquals(leonardoId, result.getContent().get(0).id());
        assertEquals(leonardoName, result.getContent().get(0).name());
        assertEquals(leonardoBiography, result.getContent().get(0).biography());

        ArgumentCaptor<ArtistNameRequest> requestCaptor = ArgumentCaptor.forClass(ArtistNameRequest.class);
        verify(stub).getArtistsByName(requestCaptor.capture());

        ArtistNameRequest request = requestCaptor.getValue();
        assertEquals(leonardoName, request.getName());
        assertEquals(0, request.getPageable().getPage());
        assertEquals(10, request.getPageable().getSize());

        ArgumentCaptor<EventJson> eventCaptor = ArgumentCaptor.forClass(EventJson.class);
        verify(kafkaTemplate).send(eq("events"), eventCaptor.capture());

        EventJson event = eventCaptor.getValue();
        assertNotNull(event.date());
        assertEquals(GET, event.eventType());
        assertEquals("Get artists by name: " + leonardoName, event.description());
        assertNull(event.entityId());
        assertEquals(username, event.username());
    }

    @Test
    void getArtistsByNameShouldThrowBadRequestWhenNameIsNull() {
        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> client.getArtistsByName(null, PageRequest.of(0, 10)));

        assertEquals(400, exception.getStatusCode().value());
        assertEquals("Artist name is required", exception.getReason());

        verifyNoInteractions(stub);
        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void getArtistsByNameShouldThrowBadRequestWhenNameIsBlank() {
        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> client.getArtistsByName("   ", PageRequest.of(0, 10)));

        assertEquals(400, exception.getStatusCode().value());
        assertEquals("Artist name is required", exception.getReason());

        verifyNoInteractions(stub);
        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void getArtistsByNameShouldThrowGrpcStatusException() {
        when(stub.getArtistsByName(any(ArtistNameRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        PageRequest pageable = PageRequest.of(0, 10);

        assertThrows(GrpcStatusException.class, () -> client.getArtistsByName(leonardoName, pageable));

        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }
}