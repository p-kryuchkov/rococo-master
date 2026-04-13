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

import static io.student.rococo.model.EventType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GrpcPaintingClientTest {

    private final UUID paintingId = UUID.randomUUID();
    private final UUID secondPaintingId = UUID.randomUUID();
    private final UUID artistId = UUID.randomUUID();
    private final UUID museumId = UUID.randomUUID();

    private final String username = "splinter";
    private final String title = "Mona Lisa";
    private final String secondTitle = "The Ninth Wave";
    private final String description = "Painting description";

    private final KafkaTemplate<String, EventJson> kafkaTemplate = mock(KafkaTemplate.class);
    private final CurrentUserProvider currentUserProvider = mock(CurrentUserProvider.class);
    private final PaintingServiceGrpc.PaintingServiceBlockingStub stub =
            mock(PaintingServiceGrpc.PaintingServiceBlockingStub.class);

    private final GrpcPaintingClient client = new GrpcPaintingClient(kafkaTemplate, currentUserProvider);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(client, "stub", stub);
        when(currentUserProvider.getUsername()).thenReturn(username);
    }

    @Test
    void getAllPaintings() {
        final PaintingResponse firstPainting = PaintingResponse.newBuilder()
                .setId(paintingId.toString())
                .setTitle(title)
                .setDescription(description)
                .build();

        final PaintingResponse secondPainting = PaintingResponse.newBuilder()
                .setId(secondPaintingId.toString())
                .setTitle(secondTitle)
                .setDescription(description)
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
        assertEquals(title, result.getContent().get(0).title());
        assertEquals(secondTitle, result.getContent().get(1).title());

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
        assertEquals(username, event.username());
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
                .setId(paintingId.toString())
                .setTitle(title)
                .setDescription(description)
                .build();

        when(stub.findPaintingById(any(IdRequest.class))).thenReturn(response);

        PaintingJson result = client.getPaintingById(paintingId);

        assertNotNull(result);
        assertEquals(paintingId, result.id());
        assertEquals(title, result.title());
        assertEquals(description, result.description());

        ArgumentCaptor<IdRequest> requestCaptor = ArgumentCaptor.forClass(IdRequest.class);
        verify(stub).findPaintingById(requestCaptor.capture());
        assertEquals(paintingId.toString(), requestCaptor.getValue().getId());

        ArgumentCaptor<EventJson> eventCaptor = ArgumentCaptor.forClass(EventJson.class);
        verify(kafkaTemplate).send(eq("events"), eventCaptor.capture());

        EventJson event = eventCaptor.getValue();
        assertNotNull(event.date());
        assertEquals(GET, event.eventType());
        assertEquals("Get Painting by Id", event.description());
        assertEquals(paintingId, event.entityId());
        assertEquals(username, event.username());
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

        assertThrows(GrpcStatusException.class, () -> client.getPaintingById(paintingId));

        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void getPaintingsByArtist() {
        final PaintingResponse firstPainting = PaintingResponse.newBuilder()
                .setId(paintingId.toString())
                .setTitle(title)
                .setDescription(description)
                .build();

        final PaintingResponse secondPainting = PaintingResponse.newBuilder()
                .setId(secondPaintingId.toString())
                .setTitle(secondTitle)
                .setDescription(description)
                .build();

        final PaintingsResponse response = PaintingsResponse.newBuilder()
                .addPaintings(firstPainting)
                .addPaintings(secondPainting)
                .setTotalElements(4)
                .build();

        when(stub.findPaintingByArtist(any(PaintingsByArtistRequest.class))).thenReturn(response);

        PageRequest pageable = PageRequest.of(0, 2);

        Page<PaintingJson> result = client.getPaintingsByArtist(artistId, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(4, result.getTotalElements());
        assertEquals(title, result.getContent().get(0).title());
        assertEquals(secondTitle, result.getContent().get(1).title());

        ArgumentCaptor<PaintingsByArtistRequest> requestCaptor =
                ArgumentCaptor.forClass(PaintingsByArtistRequest.class);
        verify(stub).findPaintingByArtist(requestCaptor.capture());

        PaintingsByArtistRequest request = requestCaptor.getValue();
        assertEquals(artistId.toString(), request.getArtistId());
        assertEquals(0, request.getPageable().getPage());
        assertEquals(2, request.getPageable().getSize());

        ArgumentCaptor<EventJson> eventCaptor = ArgumentCaptor.forClass(EventJson.class);
        verify(kafkaTemplate).send(eq("events"), eventCaptor.capture());

        EventJson event = eventCaptor.getValue();
        assertNotNull(event.date());
        assertEquals(GET, event.eventType());
        assertEquals("Get Paintings by Artist", event.description());
        assertEquals(artistId, event.entityId());
        assertEquals(username, event.username());
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

        assertThrows(GrpcStatusException.class, () -> client.getPaintingsByArtist(artistId, pageable));

        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void createPaintingWithContent() {
        PaintingJson painting = mock(PaintingJson.class, RETURNS_DEEP_STUBS);

        when(painting.title()).thenReturn(title);
        when(painting.description()).thenReturn(description);
        when(painting.artist().id()).thenReturn(artistId);
        when(painting.museum().id()).thenReturn(museumId);

        byte[] contentBytes = "painting-content".getBytes(StandardCharsets.UTF_8);
        String base64Content = "data:image/png;base64," + Base64.getEncoder().encodeToString(contentBytes);
        when(painting.content()).thenReturn(base64Content);

        final PaintingResponse response = PaintingResponse.newBuilder()
                .setId(paintingId.toString())
                .setTitle(title)
                .setDescription(description)
                .build();

        when(stub.createPainting(any(CreatePaintingRequest.class))).thenReturn(response);

        PaintingJson result = client.createPainting(painting);

        assertNotNull(result);
        assertEquals(paintingId, result.id());
        assertEquals(title, result.title());
        assertEquals(description, result.description());

        ArgumentCaptor<CreatePaintingRequest> requestCaptor =
                ArgumentCaptor.forClass(CreatePaintingRequest.class);
        verify(stub).createPainting(requestCaptor.capture());

        CreatePaintingRequest request = requestCaptor.getValue();
        assertEquals(title, request.getTitle());
        assertEquals(description, request.getDescription());
        assertEquals(ByteString.copyFrom(contentBytes), request.getContent());
        assertEquals(artistId.toString(), request.getArtist().getId());
        assertEquals(museumId.toString(), request.getMuseum().getId());

        ArgumentCaptor<EventJson> eventCaptor = ArgumentCaptor.forClass(EventJson.class);
        verify(kafkaTemplate).send(eq("events"), eventCaptor.capture());

        EventJson event = eventCaptor.getValue();
        assertNotNull(event.date());
        assertEquals(CREATE, event.eventType());
        assertEquals("Create Painting", event.description());
        assertEquals(paintingId, event.entityId());
        assertEquals(username, event.username());
    }

    @Test
    void createPaintingWithoutContent() {
        PaintingJson painting = mock(PaintingJson.class, RETURNS_DEEP_STUBS);

        when(painting.title()).thenReturn(title);
        when(painting.description()).thenReturn(description);
        when(painting.artist().id()).thenReturn(artistId);
        when(painting.museum().id()).thenReturn(museumId);
        when(painting.content()).thenReturn(null);

        final PaintingResponse response = PaintingResponse.newBuilder()
                .setId(paintingId.toString())
                .setTitle(title)
                .setDescription(description)
                .build();

        when(stub.createPainting(any(CreatePaintingRequest.class))).thenReturn(response);

        client.createPainting(painting);

        ArgumentCaptor<CreatePaintingRequest> requestCaptor =
                ArgumentCaptor.forClass(CreatePaintingRequest.class);
        verify(stub).createPainting(requestCaptor.capture());

        CreatePaintingRequest request = requestCaptor.getValue();
        assertEquals(title, request.getTitle());
        assertEquals(description, request.getDescription());
        assertTrue(request.getContent().isEmpty());
        assertEquals(artistId.toString(), request.getArtist().getId());
        assertEquals(museumId.toString(), request.getMuseum().getId());
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

        when(painting.title()).thenReturn(title);
        when(painting.description()).thenReturn(description);
        when(painting.artist().id()).thenReturn(artistId);
        when(painting.museum().id()).thenReturn(museumId);
        when(painting.content()).thenReturn(null);

        when(stub.createPainting(any(CreatePaintingRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        assertThrows(GrpcStatusException.class, () -> client.createPainting(painting));

        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void updatePainting() {
        PaintingJson painting = mock(PaintingJson.class, RETURNS_DEEP_STUBS);

        when(painting.id()).thenReturn(paintingId);
        when(painting.title()).thenReturn(title);
        String updatedDescription = "Updated painting description";
        when(painting.description()).thenReturn(updatedDescription);
        when(painting.artist().id()).thenReturn(artistId);
        when(painting.museum().id()).thenReturn(museumId);

        byte[] contentBytes = "updated-painting-content".getBytes(StandardCharsets.UTF_8);
        String base64Content = "data:image/png;base64," + Base64.getEncoder().encodeToString(contentBytes);
        when(painting.content()).thenReturn(base64Content);

        final PaintingResponse response = PaintingResponse.newBuilder()
                .setId(paintingId.toString())
                .setTitle(title)
                .setDescription(updatedDescription)
                .build();

        when(stub.updatePainting(any(UpdatePaintingRequest.class))).thenReturn(response);

        PaintingJson result = client.updatePainting(painting);

        assertNotNull(result);
        assertEquals(paintingId, result.id());
        assertEquals(title, result.title());
        assertEquals(updatedDescription, result.description());

        ArgumentCaptor<UpdatePaintingRequest> requestCaptor =
                ArgumentCaptor.forClass(UpdatePaintingRequest.class);
        verify(stub).updatePainting(requestCaptor.capture());

        UpdatePaintingRequest request = requestCaptor.getValue();
        assertEquals(paintingId.toString(), request.getId());
        assertEquals(title, request.getTitle());
        assertEquals(updatedDescription, request.getDescription());
        assertEquals(ByteString.copyFrom(contentBytes), request.getContent());
        assertEquals(artistId.toString(), request.getArtist().getId());
        assertEquals(museumId.toString(), request.getMuseum().getId());

        ArgumentCaptor<EventJson> eventCaptor = ArgumentCaptor.forClass(EventJson.class);
        verify(kafkaTemplate).send(eq("events"), eventCaptor.capture());

        EventJson event = eventCaptor.getValue();
        assertNotNull(event.date());
        assertEquals(UPDATE, event.eventType());
        assertEquals("Update Painting", event.description());
        assertEquals(paintingId, event.entityId());
        assertEquals(username, event.username());
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

        when(painting.id()).thenReturn(paintingId);
        when(painting.title()).thenReturn(title);
        when(painting.description()).thenReturn(description);
        when(painting.artist().id()).thenReturn(artistId);
        when(painting.museum().id()).thenReturn(museumId);
        when(painting.content()).thenReturn(null);

        when(stub.updatePainting(any(UpdatePaintingRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        assertThrows(GrpcStatusException.class, () -> client.updatePainting(painting));

        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void getPaintingsByTitle() {
        final PaintingResponse firstPainting = PaintingResponse.newBuilder()
                .setId(paintingId.toString())
                .setTitle(title)
                .setDescription(description)
                .build();

        final PaintingResponse secondPainting = PaintingResponse.newBuilder()
                .setId(secondPaintingId.toString())
                .setTitle(secondTitle)
                .setDescription(description)
                .build();

        final PaintingsResponse response = PaintingsResponse.newBuilder()
                .addPaintings(firstPainting)
                .addPaintings(secondPainting)
                .setTotalElements(7)
                .build();

        when(stub.findPaintingsByName(any(PaintingTitleRequest.class))).thenReturn(response);

        PageRequest pageable = PageRequest.of(1, 2);

        Page<PaintingJson> result = client.getPaintingsByTitle(title, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(7, result.getTotalElements());
        assertEquals(title, result.getContent().get(0).title());
        assertEquals(secondTitle, result.getContent().get(1).title());

        ArgumentCaptor<PaintingTitleRequest> requestCaptor =
                ArgumentCaptor.forClass(PaintingTitleRequest.class);
        verify(stub).findPaintingsByName(requestCaptor.capture());

        PaintingTitleRequest request = requestCaptor.getValue();
        assertEquals(title, request.getTitle());
        assertEquals(1, request.getPageable().getPage());
        assertEquals(2, request.getPageable().getSize());

        ArgumentCaptor<EventJson> eventCaptor = ArgumentCaptor.forClass(EventJson.class);
        verify(kafkaTemplate).send(eq("events"), eventCaptor.capture());

        EventJson event = eventCaptor.getValue();
        assertNotNull(event.date());
        assertEquals(GET, event.eventType());
        assertEquals("Get Paintings by Title", event.description());
        assertNull(event.entityId());
        assertEquals(username, event.username());
    }

    @Test
    void getPaintingsByTitleShouldThrowBadRequestWhenTitleIsNull() {
        PageRequest pageable = PageRequest.of(0, 10);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> client.getPaintingsByTitle(null, pageable));

        assertEquals(400, exception.getStatusCode().value());
        assertEquals("Painting title is required", exception.getReason());

        verifyNoInteractions(stub);
        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void getPaintingsByTitleShouldThrowGrpcStatusException() {
        when(stub.findPaintingsByName(any(PaintingTitleRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        PageRequest pageable = PageRequest.of(0, 10);

        assertThrows(GrpcStatusException.class, () -> client.getPaintingsByTitle(title, pageable));

        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }
}