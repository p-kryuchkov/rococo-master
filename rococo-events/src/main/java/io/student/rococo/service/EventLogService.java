package io.student.rococo.service;

import io.student.rococo.data.entity.EventEntity;
import io.student.rococo.data.repository.EventRepository;
import io.student.rococo.model.EventJson;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EventLogService {
    private final EventRepository eventRepository;

    @Autowired
    public EventLogService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    @KafkaListener(topics = "events", groupId = "events")
    public void listener(@Payload EventJson event, ConsumerRecord<String, EventJson> cr) {
        EventEntity eventEntity = new EventEntity();
        eventEntity.setEventType(event.eventType());
        eventEntity.setDescription(event.description());
        eventEntity.setEventTime(event.date());
        eventEntity.setUsername(event.username());
        eventEntity.setEntityId(event.entityId());
        eventRepository.save(eventEntity);
    }
}
