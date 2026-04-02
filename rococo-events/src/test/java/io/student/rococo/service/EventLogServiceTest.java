package io.student.rococo.service;

import io.student.rococo.data.entity.EventEntity;
import io.student.rococo.data.repository.EventRepository;
import io.student.rococo.model.EventJson;
import io.student.rococo.model.EventType;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventLogServiceTest {
    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    private EventLogService eventLogService;

    @Test
    void saveEventWithAllFields() {
        final EventJson event = mock(EventJson.class);
        final ConsumerRecord<String, EventJson> cr = mock(ConsumerRecord.class);
        final String description = "Event Description";
        final Instant date = Instant.parse("1999-11-11T11:22:00Z");
        final String username = "Splinter";
        final UUID entityId = UUID.randomUUID();

        when(event.eventType()).thenReturn(EventType.CREATE);
        when(event.description()).thenReturn(description);
        when(event.date()).thenReturn(date);
        when(event.username()).thenReturn(username);
        when(event.entityId()).thenReturn(entityId);

        eventLogService.listener(event, cr);

        ArgumentCaptor<EventEntity> eventCaptor = ArgumentCaptor.forClass(EventEntity.class);
        verify(eventRepository).save(eventCaptor.capture());

        EventEntity savedEvent = eventCaptor.getValue();

        assertNotNull(savedEvent);
        assertEquals(EventType.CREATE, savedEvent.getEventType());
        assertEquals(description, savedEvent.getDescription());
        assertEquals(date, savedEvent.getEventTime());
        assertEquals(username, savedEvent.getUsername());
        assertEquals(entityId, savedEvent.getEntityId());
    }

    @Test
    void saveEventWithRequiredFields() {
        final EventJson event = mock(EventJson.class);
        final ConsumerRecord<String, EventJson> cr = mock(ConsumerRecord.class);
        final Instant date = Instant.parse("1999-11-11T11:22:00Z");

        when(event.eventType()).thenReturn(EventType.CREATE);
        when(event.date()).thenReturn(date);

        eventLogService.listener(event, cr);

        ArgumentCaptor<EventEntity> eventCaptor = ArgumentCaptor.forClass(EventEntity.class);
        verify(eventRepository).save(eventCaptor.capture());

        EventEntity savedEvent = eventCaptor.getValue();

        assertNotNull(savedEvent);
        assertEquals(EventType.CREATE, savedEvent.getEventType());
        assertNull(savedEvent.getDescription());
        assertEquals(date, savedEvent.getEventTime());
        assertNull(savedEvent.getUsername());
        assertNull(savedEvent.getEntityId());
    }
}