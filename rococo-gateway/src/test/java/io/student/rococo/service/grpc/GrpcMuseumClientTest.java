package io.student.rococo.service.grpc;

import com.google.protobuf.ByteString;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.student.rococo.exception.GrpcStatusException;
import io.student.rococo.grpc.*;
import io.student.rococo.model.EventJson;
import io.student.rococo.model.MuseumJson;
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

class GrpcMuseumClientTest {

    private static final UUID LOUVRE_ID = UUID.randomUUID();
    private static final UUID PRADO_ID = UUID.randomUUID();
    private static final UUID FRANCE_ID = UUID.randomUUID();
    private static final UUID SPAIN_ID = UUID.randomUUID();

    private static final String USERNAME = "splinter";
    private static final String LOUVRE_TITLE = "Louvre";
    private static final String PRADO_TITLE = "Prado";
    private static final String LOUVRE_DESCRIPTION = "Louvre description";
    private static final String PRADO_DESCRIPTION = "Prado description";
    private static final String UPDATED_DESCRIPTION = "Updated museum description";
    private static final String PARIS = "Paris";
    private static final String MADRID = "Madrid";

    private final KafkaTemplate<String, EventJson> kafkaTemplate = mock(KafkaTemplate.class);
    private final CurrentUserProvider currentUserProvider = mock(CurrentUserProvider.class);
    private final MuseumServiceGrpc.MuseumServiceBlockingStub stub =
            mock(MuseumServiceGrpc.MuseumServiceBlockingStub.class);

    private final GrpcMuseumClient client = new GrpcMuseumClient(kafkaTemplate, currentUserProvider);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(client, "stub", stub);
        when(currentUserProvider.getUsername()).thenReturn(USERNAME);
    }

    @Test
    void getAllMuseums() {
        final MuseumResponse louvre = MuseumResponse.newBuilder()
                .setId(LOUVRE_ID.toString())
                .setTitle(LOUVRE_TITLE)
                .setDescription(LOUVRE_DESCRIPTION)
                .setGeo(Geo.newBuilder()
                        .setCity(PARIS)
                        .setCountryId(FRANCE_ID.toString())
                        .build())
                .build();

        final MuseumResponse prado = MuseumResponse.newBuilder()
                .setId(PRADO_ID.toString())
                .setTitle(PRADO_TITLE)
                .setDescription(PRADO_DESCRIPTION)
                .setGeo(Geo.newBuilder()
                        .setCity(MADRID)
                        .setCountryId(SPAIN_ID.toString())
                        .build())
                .build();

        final MuseumsResponse response = MuseumsResponse.newBuilder()
                .addMuseums(louvre)
                .addMuseums(prado)
                .setTotalElements(5)
                .build();

        when(stub.allMuseums(any(PageableRequest.class))).thenReturn(response);

        PageRequest pageable = PageRequest.of(1, 2);

        Page<MuseumJson> result = client.getAllMuseums(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(5, result.getTotalElements());
        assertEquals(LOUVRE_TITLE, result.getContent().get(0).title());
        assertEquals(PRADO_TITLE, result.getContent().get(1).title());

        ArgumentCaptor<PageableRequest> requestCaptor = ArgumentCaptor.forClass(PageableRequest.class);
        verify(stub).allMuseums(requestCaptor.capture());
        assertEquals(1, requestCaptor.getValue().getPage());
        assertEquals(2, requestCaptor.getValue().getSize());

        ArgumentCaptor<EventJson> eventCaptor = ArgumentCaptor.forClass(EventJson.class);
        verify(kafkaTemplate).send(eq("events"), eventCaptor.capture());

        EventJson event = eventCaptor.getValue();
        assertNotNull(event.date());
        assertEquals(GET, event.eventType());
        assertEquals("Get all museums", event.description());
        assertNull(event.entityId());
        assertEquals(USERNAME, event.username());
    }

    @Test
    void getAllMuseumsShouldThrowGrpcStatusException() {
        when(stub.allMuseums(any(PageableRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        PageRequest pageable = PageRequest.of(0, 20);

        assertThrows(GrpcStatusException.class, () -> client.getAllMuseums(pageable));

        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void getMuseumById() {
        final MuseumResponse response = MuseumResponse.newBuilder()
                .setId(LOUVRE_ID.toString())
                .setTitle(LOUVRE_TITLE)
                .setDescription(LOUVRE_DESCRIPTION)
                .setGeo(Geo.newBuilder()
                        .setCity(PARIS)
                        .setCountryId(FRANCE_ID.toString())
                        .build())
                .build();

        when(stub.findMuseumById(any(IdRequest.class))).thenReturn(response);

        MuseumJson result = client.getMuseumById(LOUVRE_ID);

        assertNotNull(result);
        assertEquals(LOUVRE_ID, result.id());
        assertEquals(LOUVRE_TITLE, result.title());
        assertEquals(LOUVRE_DESCRIPTION, result.description());

        ArgumentCaptor<IdRequest> requestCaptor = ArgumentCaptor.forClass(IdRequest.class);
        verify(stub).findMuseumById(requestCaptor.capture());
        assertEquals(LOUVRE_ID.toString(), requestCaptor.getValue().getId());

        ArgumentCaptor<EventJson> eventCaptor = ArgumentCaptor.forClass(EventJson.class);
        verify(kafkaTemplate).send(eq("events"), eventCaptor.capture());

        EventJson event = eventCaptor.getValue();
        assertNotNull(event.date());
        assertEquals(GET, event.eventType());
        assertEquals("Get museum by ID", event.description());
        assertEquals(LOUVRE_ID, event.entityId());
        assertEquals(USERNAME, event.username());
    }

    @Test
    void getMuseumByIdShouldThrowBadRequestWhenIdIsNull() {
        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> client.getMuseumById(null));

        assertEquals(400, exception.getStatusCode().value());
        assertEquals("Museum id is required", exception.getReason());

        verifyNoInteractions(stub);
        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void getMuseumByIdShouldThrowGrpcStatusException() {
        when(stub.findMuseumById(any(IdRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.NOT_FOUND));

        assertThrows(GrpcStatusException.class, () -> client.getMuseumById(LOUVRE_ID));

        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void createMuseumWithPhoto() {
        MuseumJson museum = mock(MuseumJson.class, RETURNS_DEEP_STUBS);
        when(museum.title()).thenReturn(LOUVRE_TITLE);
        when(museum.description()).thenReturn(LOUVRE_DESCRIPTION);
        when(museum.geo().city()).thenReturn(PARIS);
        when(museum.geo().country().id()).thenReturn(FRANCE_ID);

        byte[] photoBytes = "museum-photo".getBytes(StandardCharsets.UTF_8);
        String base64Photo = "data:image/png;base64," + Base64.getEncoder().encodeToString(photoBytes);
        when(museum.photo()).thenReturn(base64Photo);

        final MuseumResponse response = MuseumResponse.newBuilder()
                .setId(LOUVRE_ID.toString())
                .setTitle(LOUVRE_TITLE)
                .setDescription(LOUVRE_DESCRIPTION)
                .setGeo(Geo.newBuilder()
                        .setCity(PARIS)
                        .setCountryId(FRANCE_ID.toString())
                        .build())
                .build();

        when(stub.createMuseum(any(CreateMuseumRequest.class))).thenReturn(response);

        MuseumJson result = client.createMuseum(museum);

        assertNotNull(result);
        assertEquals(LOUVRE_ID, result.id());
        assertEquals(LOUVRE_TITLE, result.title());
        assertEquals(LOUVRE_DESCRIPTION, result.description());

        ArgumentCaptor<CreateMuseumRequest> requestCaptor = ArgumentCaptor.forClass(CreateMuseumRequest.class);
        verify(stub).createMuseum(requestCaptor.capture());

        CreateMuseumRequest request = requestCaptor.getValue();
        assertEquals(LOUVRE_TITLE, request.getTitle());
        assertEquals(LOUVRE_DESCRIPTION, request.getDescription());
        assertEquals(PARIS, request.getGeo().getCity());
        assertEquals(FRANCE_ID.toString(), request.getGeo().getCountryId());
        assertEquals(ByteString.copyFrom(photoBytes), request.getPhoto());

        ArgumentCaptor<EventJson> eventCaptor = ArgumentCaptor.forClass(EventJson.class);
        verify(kafkaTemplate).send(eq("events"), eventCaptor.capture());

        EventJson event = eventCaptor.getValue();
        assertNotNull(event.date());
        assertEquals(CREATE, event.eventType());
        assertEquals("Create museum", event.description());
        assertEquals(LOUVRE_ID, event.entityId());
        assertEquals(USERNAME, event.username());
    }

    @Test
    void createMuseumWithoutPhoto() {
        MuseumJson museum = mock(MuseumJson.class, RETURNS_DEEP_STUBS);
        when(museum.title()).thenReturn(LOUVRE_TITLE);
        when(museum.description()).thenReturn(LOUVRE_DESCRIPTION);
        when(museum.geo().city()).thenReturn(PARIS);
        when(museum.geo().country().id()).thenReturn(FRANCE_ID);
        when(museum.photo()).thenReturn(null);

        final MuseumResponse response = MuseumResponse.newBuilder()
                .setId(LOUVRE_ID.toString())
                .setTitle(LOUVRE_TITLE)
                .setDescription(LOUVRE_DESCRIPTION)
                .setGeo(Geo.newBuilder()
                        .setCity(PARIS)
                        .setCountryId(FRANCE_ID.toString())
                        .build())
                .build();

        when(stub.createMuseum(any(CreateMuseumRequest.class))).thenReturn(response);

        client.createMuseum(museum);

        ArgumentCaptor<CreateMuseumRequest> requestCaptor = ArgumentCaptor.forClass(CreateMuseumRequest.class);
        verify(stub).createMuseum(requestCaptor.capture());

        CreateMuseumRequest request = requestCaptor.getValue();
        assertEquals(LOUVRE_TITLE, request.getTitle());
        assertEquals(LOUVRE_DESCRIPTION, request.getDescription());
        assertEquals(PARIS, request.getGeo().getCity());
        assertEquals(FRANCE_ID.toString(), request.getGeo().getCountryId());
        assertTrue(request.getPhoto().isEmpty());
    }

    @Test
    void updateMuseumShouldThrowBadRequestWhenIdIsNull() {
        MuseumJson museum = mock(MuseumJson.class);
        when(museum.id()).thenReturn(null);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> client.updateMuseum(museum));

        assertEquals(400, exception.getStatusCode().value());
        assertEquals("Museum id is required", exception.getReason());

        verifyNoInteractions(stub);
        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }

    @Test
    void updateMuseum() {
        MuseumJson museum = mock(MuseumJson.class, RETURNS_DEEP_STUBS);
        when(museum.id()).thenReturn(LOUVRE_ID);
        when(museum.title()).thenReturn(LOUVRE_TITLE);
        when(museum.description()).thenReturn(UPDATED_DESCRIPTION);
        when(museum.geo().city()).thenReturn(PARIS);
        when(museum.geo().country().id()).thenReturn(FRANCE_ID);

        byte[] photoBytes = "updated-museum-photo".getBytes(StandardCharsets.UTF_8);
        String base64Photo = "data:image/png;base64," + Base64.getEncoder().encodeToString(photoBytes);
        when(museum.photo()).thenReturn(base64Photo);

        final MuseumResponse response = MuseumResponse.newBuilder()
                .setId(LOUVRE_ID.toString())
                .setTitle(LOUVRE_TITLE)
                .setDescription(UPDATED_DESCRIPTION)
                .setGeo(Geo.newBuilder()
                        .setCity(PARIS)
                        .setCountryId(FRANCE_ID.toString())
                        .build())
                .build();

        when(stub.updateMuseum(any(UpdateMuseumRequest.class))).thenReturn(response);

        MuseumJson result = client.updateMuseum(museum);

        assertNotNull(result);
        assertEquals(LOUVRE_ID, result.id());
        assertEquals(LOUVRE_TITLE, result.title());
        assertEquals(UPDATED_DESCRIPTION, result.description());

        ArgumentCaptor<UpdateMuseumRequest> requestCaptor = ArgumentCaptor.forClass(UpdateMuseumRequest.class);
        verify(stub).updateMuseum(requestCaptor.capture());

        UpdateMuseumRequest request = requestCaptor.getValue();
        assertEquals(LOUVRE_ID.toString(), request.getId());
        assertEquals(LOUVRE_TITLE, request.getTitle());
        assertEquals(UPDATED_DESCRIPTION, request.getDescription());
        assertEquals(PARIS, request.getGeo().getCity());
        assertEquals(FRANCE_ID.toString(), request.getGeo().getCountryId());
        assertEquals(ByteString.copyFrom(photoBytes), request.getPhoto());

        ArgumentCaptor<EventJson> eventCaptor = ArgumentCaptor.forClass(EventJson.class);
        verify(kafkaTemplate).send(eq("events"), eventCaptor.capture());

        EventJson event = eventCaptor.getValue();
        assertNotNull(event.date());
        assertEquals(UPDATE, event.eventType());
        assertEquals("Update museum", event.description());
        assertEquals(LOUVRE_ID, event.entityId());
        assertEquals(USERNAME, event.username());
    }

    @Test
    void updateMuseumShouldThrowGrpcStatusException() {
        MuseumJson museum = mock(MuseumJson.class, RETURNS_DEEP_STUBS);
        when(museum.id()).thenReturn(LOUVRE_ID);
        when(museum.title()).thenReturn(LOUVRE_TITLE);
        when(museum.description()).thenReturn(LOUVRE_DESCRIPTION);
        when(museum.geo().city()).thenReturn(PARIS);
        when(museum.geo().country().id()).thenReturn(FRANCE_ID);
        when(museum.photo()).thenReturn(null);

        when(stub.updateMuseum(any(UpdateMuseumRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        assertThrows(GrpcStatusException.class, () -> client.updateMuseum(museum));

        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }
}