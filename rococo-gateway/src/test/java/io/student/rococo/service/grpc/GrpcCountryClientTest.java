package io.student.rococo.service.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.student.rococo.exception.GrpcStatusException;
import io.student.rococo.grpc.CountriesResponse;
import io.student.rococo.grpc.CountriesServiceGrpc;
import io.student.rococo.grpc.CountryResponse;
import io.student.rococo.grpc.PageableRequest;
import io.student.rococo.model.CountryJson;
import io.student.rococo.model.EventJson;
import io.student.rococo.utils.CurrentUserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static io.student.rococo.model.EventType.GET;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GrpcCountryClientTest {

    private final String username = "splinter";
    private final UUID franceId = UUID.randomUUID();
    private final UUID spainId = UUID.randomUUID();


    private final KafkaTemplate<String, EventJson> kafkaTemplate = mock(KafkaTemplate.class);
    private final CurrentUserProvider currentUserProvider = mock(CurrentUserProvider.class);
    private final CountriesServiceGrpc.CountriesServiceBlockingStub stub =
            mock(CountriesServiceGrpc.CountriesServiceBlockingStub.class);

    private final GrpcCountryClient client = new GrpcCountryClient(kafkaTemplate, currentUserProvider);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(client, "stub", stub);
        when(currentUserProvider.getUsername()).thenReturn(username);
    }

    @Test
    void getAllCountries() {
        String franceName = "France";
        final CountryResponse firstCountry = CountryResponse.newBuilder()
                .setId(franceId.toString())
                .setName(franceName)
                .build();
        String spainName = "Spain";
        final CountryResponse secondCountry = CountryResponse.newBuilder()
                .setId(spainId.toString())
                .setName(spainName)
                .build();

        final CountriesResponse response = CountriesResponse.newBuilder()
                .addCountries(firstCountry)
                .addCountries(secondCountry)
                .setTotalElements(5)
                .build();

        when(stub.allCountries(any(PageableRequest.class))).thenReturn(response);

        PageRequest pageable = PageRequest.of(1, 2);

        Page<CountryJson> result = client.getAllCountries(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(5, result.getTotalElements());

        final CountryJson firstResult = result.getContent().get(0);
        final CountryJson secondResult = result.getContent().get(1);

        assertNotNull(firstResult);
        assertEquals(franceId, firstResult.id());
        assertEquals(franceName, firstResult.name());

        assertNotNull(secondResult);
        assertEquals(spainId, secondResult.id());
        assertEquals(spainName, secondResult.name());

        ArgumentCaptor<PageableRequest> requestCaptor = ArgumentCaptor.forClass(PageableRequest.class);
        verify(stub).allCountries(requestCaptor.capture());
        assertEquals(1, requestCaptor.getValue().getPage());
        assertEquals(2, requestCaptor.getValue().getSize());

        ArgumentCaptor<EventJson> eventCaptor = ArgumentCaptor.forClass(EventJson.class);
        verify(kafkaTemplate).send(eq("events"), eventCaptor.capture());

        EventJson event = eventCaptor.getValue();
        assertNotNull(event.date());
        assertEquals(GET, event.eventType());
        assertEquals("Get all countries", event.description());
        assertNull(event.entityId());
        assertEquals(username, event.username());
    }

    @Test
    void getAllCountriesShouldThrowGrpcStatusException() {
        when(stub.allCountries(any(PageableRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        PageRequest pageable = PageRequest.of(0, 20);

        assertThrows(GrpcStatusException.class, () -> client.getAllCountries(pageable));

        verify(kafkaTemplate, never()).send(anyString(), any(EventJson.class));
    }
}