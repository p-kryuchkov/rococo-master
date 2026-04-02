package io.student.rococo.service.grpc;

import com.google.protobuf.ByteString;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.student.rococo.exception.GrpcStatusException;
import io.student.rococo.grpc.*;
import io.student.rococo.model.EventJson;
import io.student.rococo.model.PaintingJson;
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

class GrpcPaintingClientTest {

    private static final UUID PAINTING_ID = UUID.randomUUID();
    private static final UUID SECOND_PAINTING_ID = UUID.randomUUID();
    private static final UUID ARTIST_ID = UUID.randomUUID();
    private static final UUID MUSEUM_ID = UUID.randomUUID();

    private static final String USERNAME = "splinter";
    private static final String TITLE = "Mona Lisa";
    private static final String SECOND_TITLE = "The Ninth Wave";
    private static final String DESCRIPTION = "Painting description";
    private static final String UPDATED_DESCRIPTION = "Updated painting description";

    private final KafkaTemplate<String, EventJson> kafkaTemplate = mock(KafkaTemplate.class);
    private final CurrentUserProvider currentUserProvider = mock(CurrentUserProvider.class);
    private final PaintingServiceGrpc.PaintingServiceBlockingStub stub =
            mock(PaintingServiceGrpc.PaintingServiceBlockingStub.class);

    private final GrpcPaintingClient client = new GrpcPaintingClient(kafkaTemplate, currentUserProvider);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(client, "stub", stub);
        when(currentUserProvider.getUsername()).thenReturn(USERNAME);
    }

    @Test
    void getAllPaintings() {
        final PaintingResponse firstPainting = PaintingResponse.newBuilder()
                .setId(PAINTING_ID.toString())
                .setTitle(TITLE)
                .setDescription(DESCRIPTION)
                .build();

        final PaintingResponse secondPainting = PaintingResponse.newBuilder()
                .setId(SECOND_PAINTING_ID.toString())
                .setTitle(SECOND_TITLE)
                .setDescription(DESCRIPTION)
                .build();

        final PaintingsResponse response = PaintingsResponse.newBuilder()
                .addPaintings(firstPainting)
                .addPaintings(secondPainting)
                .setTotalElements(5)
                .build();

        when(stub.allPaintings(any(PageableRequest.class))).thenReturn(response);

        PageRequest pageable = PageRequest.of(1, 2);

        Page<PaintingJson> result = client.getAllPaintings(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(5, result.getTotalElements());
        assertEquals(TITLE, result.getContent().get(0).title());
        assertEquals(SECOND_TITLE, result.getContent().get(1).title());

        ArgumentCaptor<PageableRequest> requestCaptor = ArgumentCaptor.forClass(PageableRequest.class);
        verify(stub).allPaintings(requestCaptor.capture());
        assertEquals(1, requestCaptor.getValue().getPage());
        assertEquals(2, requestCaptor.getValue().getSize());

        ArgumentCaptor<EventJson> eventCaptor = ArgumentCaptor.forClass(EventJson.class);
        verify(kafkaTemplate).send(eq("events"), eventCaptor.capture());

        EventJson event = eventCaptor.getValue();
        assertNotNull(event.date());
        assertEquals(GET, event.eventType());
        assertEquals("Get All Paintings", event.description());
        assertNull(event.entityId());
        assertEquals(USERNAME, event.username());
    }

    @Test
    void getAllPaintingsShouldThrowGrpcStatusException() {
        when(stub.allPaintings(any(PageableRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        PageRequest pageable = PageRequest.of(0, 20);

        assertThrows(GrpcStatusException.class, () -> client.getAllPaintings(pageable));

        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void getPaintingById() {
        final PaintingResponse response = PaintingResponse.newBuilder()
                .setId(PAINTING_ID.toString())
                .setTitle(TITLE)
                .setDescription(DESCRIPTION)
                .build();

        when(stub.findPaintingById(any(IdRequest.class))).thenReturn(response);

        PaintingJson result = client.getPaintingById(PAINTING_ID);

        assertNotNull(result);
        assertEquals(PAINTING_ID, result.id());
        assertEquals(TITLE, result.title());
        assertEquals(DESCRIPTION, result.description());

        ArgumentCaptor<IdRequest> requestCaptor = ArgumentCaptor.forClass(IdRequest.class);
        verify(stub).findPaintingById(requestCaptor.capture());
        assertEquals(PAINTING_ID.toString(), requestCaptor.getValue().getId());

        ArgumentCaptor<EventJson> eventCaptor = ArgumentCaptor.forClass(EventJson.class);
        verify(kafkaTemplate).send(eq("events"), eventCaptor.capture());

        EventJson event = eventCaptor.getValue();
        assertNotNull(event.date());
        assertEquals(GET, event.eventType());
        assertEquals("Get Painting by Id", event.description());
        assertEquals(PAINTING_ID, event.entityId());
        assertEquals(USERNAME, event.username());
    }

    @Test
    void getPaintingByIdShouldThrowBadRequestWhenIdIsNull() {
        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> client.getPaintingById(null));

        assertEquals(400, exception.getStatusCode().value());
        assertEquals("Painting id is required", exception.getReason());

        verifyNoInteractions(stub);
        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void getPaintingByIdShouldThrowGrpcStatusException() {
        when(stub.findPaintingById(any(IdRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.NOT_FOUND));

        assertThrows(GrpcStatusException.class, () -> client.getPaintingById(PAINTING_ID));

        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void getPaintingsByArtist() {
        final PaintingResponse firstPainting = PaintingResponse.newBuilder()
                .setId(PAINTING_ID.toString())
                .setTitle(TITLE)
                .setDescription(DESCRIPTION)
                .build();

        final PaintingResponse secondPainting = PaintingResponse.newBuilder()
                .setId(SECOND_PAINTING_ID.toString())
                .setTitle(SECOND_TITLE)
                .setDescription(DESCRIPTION)
                .build();

        final PaintingsResponse response = PaintingsResponse.newBuilder()
                .addPaintings(firstPainting)
                .addPaintings(secondPainting)
                .setTotalElements(4)
                .build();

        when(stub.findPaintingByArtist(any(PaintingsByArtistRequest.class))).thenReturn(response);

        PageRequest pageable = PageRequest.of(0, 2);

        Page<PaintingJson> result = client.getPaintingsByArtist(ARTIST_ID, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(4, result.getTotalElements());
        assertEquals(TITLE, result.getContent().get(0).title());
        assertEquals(SECOND_TITLE, result.getContent().get(1).title());

        ArgumentCaptor<PaintingsByArtistRequest> requestCaptor =
                ArgumentCaptor.forClass(PaintingsByArtistRequest.class);
        verify(stub).findPaintingByArtist(requestCaptor.capture());

        PaintingsByArtistRequest request = requestCaptor.getValue();
        assertEquals(ARTIST_ID.toString(), request.getArtistId());
        assertEquals(0, request.getPageable().getPage());
        assertEquals(2, request.getPageable().getSize());

        ArgumentCaptor<EventJson> eventCaptor = ArgumentCaptor.forClass(EventJson.class);
        verify(kafkaTemplate).send(eq("events"), eventCaptor.capture());

        EventJson event = eventCaptor.getValue();
        assertNotNull(event.date());
        assertEquals(GET, event.eventType());
        assertEquals("Get Paintings by Artist", event.description());
        assertEquals(ARTIST_ID, event.entityId());
        assertEquals(USERNAME, event.username());
    }

    @Test
    void getPaintingsByArtistShouldThrowBadRequestWhenArtistIdIsNull() {
        PageRequest pageable = PageRequest.of(0, 2);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> client.getPaintingsByArtist(null, pageable));

        assertEquals(400, exception.getStatusCode().value());
        assertEquals("Artist id is required", exception.getReason());

        verifyNoInteractions(stub);
        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void getPaintingsByArtistShouldThrowGrpcStatusException() {
        when(stub.findPaintingByArtist(any(PaintingsByArtistRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        PageRequest pageable = PageRequest.of(0, 2);

        assertThrows(GrpcStatusException.class, () -> client.getPaintingsByArtist(ARTIST_ID, pageable));

        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void createPaintingWithContent() {
        PaintingJson painting = mock(PaintingJson.class, RETURNS_DEEP_STUBS);

        when(painting.title()).thenReturn(TITLE);
        when(painting.description()).thenReturn(DESCRIPTION);
        when(painting.artist().id()).thenReturn(ARTIST_ID);
        when(painting.museum().id()).thenReturn(MUSEUM_ID);

        byte[] contentBytes = "painting-content".getBytes(StandardCharsets.UTF_8);
        String base64Content = "data:image/png;base64," + Base64.getEncoder().encodeToString(contentBytes);
        when(painting.content()).thenReturn(base64Content);

        final PaintingResponse response = PaintingResponse.newBuilder()
                .setId(PAINTING_ID.toString())
                .setTitle(TITLE)
                .setDescription(DESCRIPTION)
                .build();

        when(stub.createPainting(any(CreatePaintingRequest.class))).thenReturn(response);

        PaintingJson result = client.createPainting(painting);

        assertNotNull(result);
        assertEquals(PAINTING_ID, result.id());
        assertEquals(TITLE, result.title());
        assertEquals(DESCRIPTION, result.description());

        ArgumentCaptor<CreatePaintingRequest> requestCaptor =
                ArgumentCaptor.forClass(CreatePaintingRequest.class);
        verify(stub).createPainting(requestCaptor.capture());

        CreatePaintingRequest request = requestCaptor.getValue();
        assertEquals(TITLE, request.getTitle());
        assertEquals(DESCRIPTION, request.getDescription());
        assertEquals(ByteString.copyFrom(contentBytes), request.getContent());
        assertEquals(ARTIST_ID.toString(), request.getArtist().getId());
        assertEquals(MUSEUM_ID.toString(), request.getMuseum().getId());

        ArgumentCaptor<EventJson> eventCaptor = ArgumentCaptor.forClass(EventJson.class);
        verify(kafkaTemplate).send(eq("events"), eventCaptor.capture());

        EventJson event = eventCaptor.getValue();
        assertNotNull(event.date());
        assertEquals(CREATE, event.eventType());
        assertEquals("Create Painting", event.description());
        assertEquals(PAINTING_ID, event.entityId());
        assertEquals(USERNAME, event.username());
    }

    @Test
    void createPaintingWithoutContent() {
        PaintingJson painting = mock(PaintingJson.class, RETURNS_DEEP_STUBS);

        when(painting.title()).thenReturn(TITLE);
        when(painting.description()).thenReturn(DESCRIPTION);
        when(painting.artist().id()).thenReturn(ARTIST_ID);
        when(painting.museum().id()).thenReturn(MUSEUM_ID);
        when(painting.content()).thenReturn(null);

        final PaintingResponse response = PaintingResponse.newBuilder()
                .setId(PAINTING_ID.toString())
                .setTitle(TITLE)
                .setDescription(DESCRIPTION)
                .build();

        when(stub.createPainting(any(CreatePaintingRequest.class))).thenReturn(response);

        client.createPainting(painting);

        ArgumentCaptor<CreatePaintingRequest> requestCaptor =
                ArgumentCaptor.forClass(CreatePaintingRequest.class);
        verify(stub).createPainting(requestCaptor.capture());

        CreatePaintingRequest request = requestCaptor.getValue();
        assertEquals(TITLE, request.getTitle());
        assertEquals(DESCRIPTION, request.getDescription());
        assertTrue(request.getContent().isEmpty());
        assertEquals(ARTIST_ID.toString(), request.getArtist().getId());
        assertEquals(MUSEUM_ID.toString(), request.getMuseum().getId());
    }

    @Test
    void createPaintingShouldThrowBadRequestWhenPaintingIsNull() {
        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> client.createPainting(null));

        assertEquals(400, exception.getStatusCode().value());
        assertEquals("Painting payload is required", exception.getReason());

        verifyNoInteractions(stub);
        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void createPaintingShouldThrowGrpcStatusException() {
        PaintingJson painting = mock(PaintingJson.class, RETURNS_DEEP_STUBS);

        when(painting.title()).thenReturn(TITLE);
        when(painting.description()).thenReturn(DESCRIPTION);
        when(painting.artist().id()).thenReturn(ARTIST_ID);
        when(painting.museum().id()).thenReturn(MUSEUM_ID);
        when(painting.content()).thenReturn(null);

        when(stub.createPainting(any(CreatePaintingRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        assertThrows(GrpcStatusException.class, () -> client.createPainting(painting));

        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void updatePainting() {
        PaintingJson painting = mock(PaintingJson.class, RETURNS_DEEP_STUBS);

        when(painting.id()).thenReturn(PAINTING_ID);
        when(painting.title()).thenReturn(TITLE);
        when(painting.description()).thenReturn(UPDATED_DESCRIPTION);
        when(painting.artist().id()).thenReturn(ARTIST_ID);
        when(painting.museum().id()).thenReturn(MUSEUM_ID);

        byte[] contentBytes = "updated-painting-content".getBytes(StandardCharsets.UTF_8);
        String base64Content = "data:image/png;base64," + Base64.getEncoder().encodeToString(contentBytes);
        when(painting.content()).thenReturn(base64Content);

        final PaintingResponse response = PaintingResponse.newBuilder()
                .setId(PAINTING_ID.toString())
                .setTitle(TITLE)
                .setDescription(UPDATED_DESCRIPTION)
                .build();

        when(stub.updatePainting(any(UpdatePaintingRequest.class))).thenReturn(response);

        PaintingJson result = client.updatePainting(painting);

        assertNotNull(result);
        assertEquals(PAINTING_ID, result.id());
        assertEquals(TITLE, result.title());
        assertEquals(UPDATED_DESCRIPTION, result.description());

        ArgumentCaptor<UpdatePaintingRequest> requestCaptor =
                ArgumentCaptor.forClass(UpdatePaintingRequest.class);
        verify(stub).updatePainting(requestCaptor.capture());

        UpdatePaintingRequest request = requestCaptor.getValue();
        assertEquals(PAINTING_ID.toString(), request.getId());
        assertEquals(TITLE, request.getTitle());
        assertEquals(UPDATED_DESCRIPTION, request.getDescription());
        assertEquals(ByteString.copyFrom(contentBytes), request.getContent());
        assertEquals(ARTIST_ID.toString(), request.getArtist().getId());
        assertEquals(MUSEUM_ID.toString(), request.getMuseum().getId());

        ArgumentCaptor<EventJson> eventCaptor = ArgumentCaptor.forClass(EventJson.class);
        verify(kafkaTemplate).send(eq("events"), eventCaptor.capture());

        EventJson event = eventCaptor.getValue();
        assertNotNull(event.date());
        assertEquals(UPDATE, event.eventType());
        assertEquals("Update Painting", event.description());
        assertEquals(PAINTING_ID, event.entityId());
        assertEquals(USERNAME, event.username());
    }

    @Test
    void updatePaintingShouldThrowBadRequestWhenIdIsNull() {
        PaintingJson painting = mock(PaintingJson.class);
        when(painting.id()).thenReturn(null);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> client.updatePainting(painting));

        assertEquals(400, exception.getStatusCode().value());
        assertEquals("Painting id is required", exception.getReason());

        verifyNoInteractions(stub);
        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void updatePaintingShouldThrowGrpcStatusException() {
        PaintingJson painting = mock(PaintingJson.class, RETURNS_DEEP_STUBS);

        when(painting.id()).thenReturn(PAINTING_ID);
        when(painting.title()).thenReturn(TITLE);
        when(painting.description()).thenReturn(DESCRIPTION);
        when(painting.artist().id()).thenReturn(ARTIST_ID);
        when(painting.museum().id()).thenReturn(MUSEUM_ID);
        when(painting.content()).thenReturn(null);

        when(stub.updatePainting(any(UpdatePaintingRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        assertThrows(GrpcStatusException.class, () -> client.updatePainting(painting));

        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }
}