package io.student.rococo.model;

import io.student.rococo.data.entity.event.EventEntity;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.UUID;

public record EventJson(Instant date, EventType eventType, String description, UUID entityId, String username) {
    public static @Nonnull EventJson fromEntity(@Nonnull final EventEntity eventEntity) {
        return new EventJson(
                eventEntity.getEventTime(),
                eventEntity.getEventType(),
                eventEntity.getDescription(),
                eventEntity.getEntityId(),
                eventEntity.getUsername()
        );
    }
}
