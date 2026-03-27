package io.student.rococo.model;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public record EventJson(Instant date, EventType eventType, String description, UUID entityId, String username) {
}
