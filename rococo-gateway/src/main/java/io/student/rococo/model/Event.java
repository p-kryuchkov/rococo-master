package io.student.rococo.model;

import java.util.Date;
import java.util.UUID;

public record Event(Date date, EventType eventType, String description, UUID entityId) {
}
