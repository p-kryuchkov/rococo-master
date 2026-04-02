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

import static io.student.rococo.model.EventType.CREATE;
import static io.student.rococo.model.EventType.GET;
import static io.student.rococo.model.EventType.UPDATE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GrpcArtistClientTest {

    private static final UUID LEONARDO_ID = UUID.randomUUID();
    private static final UUID RAPHAEL_ID = UUID.randomUUID();

    private static final String USERNAME = "splinter";
    private static final String LEONARDO_NAME = "Leonardo";
    private static final String RAPHAEL_NAME = "Raphael";
    private static final String LEONARDO_BIOGRAPHY = "Leonardo biography";
    private static final String RAPHAEL_BIOGRAPHY = "Raphael biography";
    private static final String UPDATED_BIOGRAPHY = "Updated biography";

    private final KafkaTemplate<String, EventJson> kafkaTemplate = mock(KafkaTemplate.class);
    private final CurrentUserProvider currentUserProvider = mock(CurrentUserProvider.class);
    private final ArtistServiceGrpc.ArtistServiceBlockingStub stub =
            mock(ArtistServiceGrpc.ArtistServiceBlockingStub.class);

    private final GrpcArtistClient client = new GrpcArtistClient(kafkaTemplate, currentUserProvider);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(client, "stub", stub);
        when(currentUserProvider.getUsername()).thenReturn(USERNAME);
    }

    @Test
    void getArtistById() {
        final ArtistResponse response = ArtistResponse.newBuilder()
                .setId(LEONARDO_ID.toString())
                .setName(LEONARDO_NAME)
                .setBiography(LEONARDO_BIOGRAPHY)
                .build();

        when(stub.getArtistById(any(IdRequest.class))).thenReturn(response);

        ArtistJson result = client.getArtistById(LEONARDO_ID);

        assertNotNull(result);
        assertEquals(LEONARDO_ID, result.id());
        assertEquals(LEONARDO_NAME, result.name());
        assertEquals(LEONARDO_BIOGRAPHY, result.biography());

        ArgumentCaptor<IdRequest> requestCaptor = ArgumentCaptor.forClass(IdRequest.class);
        verify(stub).getArtistById(requestCaptor.capture());
        assertEquals(LEONARDO_ID.toString(), requestCaptor.getValue().getId());

        ArgumentCaptor<EventJson> eventCaptor = ArgumentCaptor.forClass(EventJson.class);
        verify(kafkaTemplate).send(eq("events"), eventCaptor.capture());

        EventJson event = eventCaptor.getValue();
        assertNotNull(event.date());
        assertEquals(GET, event.eventType());
        assertEquals("Get artist by ID", event.description());
        assertEquals(LEONARDO_ID, event.entityId());
        assertEquals(USERNAME, event.username());
    }

    @Test
    void getArtistByIdShouldThrowGrpcStatusException() {
        when(stub.getArtistById(any(IdRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.NOT_FOUND));

        assertThrows(GrpcStatusException.class, () -> client.getArtistById(LEONARDO_ID));

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
                .setId(LEONARDO_ID.toString())
                .setName(LEONARDO_NAME)
                .setBiography(LEONARDO_BIOGRAPHY)
                .build();

        final ArtistResponse raphael = ArtistResponse.newBuilder()
                .setId(RAPHAEL_ID.toString())
                .setName(RAPHAEL_NAME)
                .setBiography(RAPHAEL_BIOGRAPHY)
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
        assertEquals(LEONARDO_NAME, result.getContent().get(0).name());
        assertEquals(RAPHAEL_NAME, result.getContent().get(1).name());

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
        assertEquals(USERNAME, event.username());
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
        when(artist.name()).thenReturn(LEONARDO_NAME);
        when(artist.biography()).thenReturn(LEONARDO_BIOGRAPHY);

        byte[] photoBytes = "leonardo-photo".getBytes(StandardCharsets.UTF_8);
        String base64Photo = "data:image/png;base64," + Base64.getEncoder().encodeToString(photoBytes);
        when(artist.photo()).thenReturn(base64Photo);

        final ArtistResponse response = ArtistResponse.newBuilder()
                .setId(LEONARDO_ID.toString())
                .setName(LEONARDO_NAME)
                .setBiography(LEONARDO_BIOGRAPHY)
                .build();

        when(stub.createArtist(any(CreateArtistRequest.class))).thenReturn(response);

        ArtistJson result = client.createArtist(artist);

        assertNotNull(result);
        assertEquals(LEONARDO_ID, result.id());
        assertEquals(LEONARDO_NAME, result.name());
        assertEquals(LEONARDO_BIOGRAPHY, result.biography());

        ArgumentCaptor<CreateArtistRequest> requestCaptor = ArgumentCaptor.forClass(CreateArtistRequest.class);
        verify(stub).createArtist(requestCaptor.capture());

        CreateArtistRequest request = requestCaptor.getValue();
        assertEquals(LEONARDO_NAME, request.getName());
        assertEquals(LEONARDO_BIOGRAPHY, request.getBiography());
        assertEquals(ByteString.copyFrom(photoBytes), request.getPhoto());

        ArgumentCaptor<EventJson> eventCaptor = ArgumentCaptor.forClass(EventJson.class);
        verify(kafkaTemplate).send(eq("events"), eventCaptor.capture());

        EventJson event = eventCaptor.getValue();
        assertNotNull(event.date());
        assertEquals(CREATE, event.eventType());
        assertEquals("Create artist", event.description());
        assertEquals(LEONARDO_ID, event.entityId());
        assertEquals(USERNAME, event.username());
    }

    @Test
    void createArtistWithoutPhoto() {
        ArtistJson artist = mock(ArtistJson.class);
        when(artist.name()).thenReturn(LEONARDO_NAME);
        when(artist.biography()).thenReturn(LEONARDO_BIOGRAPHY);
        when(artist.photo()).thenReturn(null);

        final ArtistResponse response = ArtistResponse.newBuilder()
                .setId(LEONARDO_ID.toString())
                .setName(LEONARDO_NAME)
                .setBiography(LEONARDO_BIOGRAPHY)
                .build();

        when(stub.createArtist(any(CreateArtistRequest.class))).thenReturn(response);

        client.createArtist(artist);

        ArgumentCaptor<CreateArtistRequest> requestCaptor = ArgumentCaptor.forClass(CreateArtistRequest.class);
        verify(stub).createArtist(requestCaptor.capture());

        CreateArtistRequest request = requestCaptor.getValue();
        assertEquals(LEONARDO_NAME, request.getName());
        assertEquals(LEONARDO_BIOGRAPHY, request.getBiography());
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
        when(artist.id()).thenReturn(LEONARDO_ID);
        when(artist.name()).thenReturn(LEONARDO_NAME);
        when(artist.biography()).thenReturn(UPDATED_BIOGRAPHY);

        byte[] photoBytes = "updated-leonardo-photo".getBytes(StandardCharsets.UTF_8);
        String base64Photo = "data:image/png;base64," + Base64.getEncoder().encodeToString(photoBytes);
        when(artist.photo()).thenReturn(base64Photo);

        final ArtistResponse response = ArtistResponse.newBuilder()
                .setId(LEONARDO_ID.toString())
                .setName(LEONARDO_NAME)
                .setBiography(UPDATED_BIOGRAPHY)
                .build();

        when(stub.updateArtist(any(UpdateArtistRequest.class))).thenReturn(response);

        ArtistJson result = client.updateArtist(artist);

        assertNotNull(result);
        assertEquals(LEONARDO_ID, result.id());
        assertEquals(LEONARDO_NAME, result.name());
        assertEquals(UPDATED_BIOGRAPHY, result.biography());

        ArgumentCaptor<UpdateArtistRequest> requestCaptor = ArgumentCaptor.forClass(UpdateArtistRequest.class);
        verify(stub).updateArtist(requestCaptor.capture());

        UpdateArtistRequest request = requestCaptor.getValue();
        assertEquals(LEONARDO_ID.toString(), request.getId());
        assertEquals(LEONARDO_NAME, request.getName());
        assertEquals(UPDATED_BIOGRAPHY, request.getBiography());
        assertEquals(ByteString.copyFrom(photoBytes), request.getPhoto());

        ArgumentCaptor<EventJson> eventCaptor = ArgumentCaptor.forClass(EventJson.class);
        verify(kafkaTemplate).send(eq("events"), eventCaptor.capture());

        EventJson event = eventCaptor.getValue();
        assertNotNull(event.date());
        assertEquals(UPDATE, event.eventType());
        assertEquals("Update artist", event.description());
        assertEquals(LEONARDO_ID, event.entityId());
        assertEquals(USERNAME, event.username());
    }

    @Test
    void updateArtistShouldThrowGrpcStatusException() {
        ArtistJson artist = mock(ArtistJson.class);
        when(artist.id()).thenReturn(LEONARDO_ID);
        when(artist.name()).thenReturn(LEONARDO_NAME);
        when(artist.biography()).thenReturn(LEONARDO_BIOGRAPHY);
        when(artist.photo()).thenReturn(null);

        when(stub.updateArtist(any(UpdateArtistRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        assertThrows(GrpcStatusException.class, () -> client.updateArtist(artist));

        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }
}