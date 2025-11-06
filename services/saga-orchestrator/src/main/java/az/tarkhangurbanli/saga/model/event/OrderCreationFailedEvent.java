package az.tarkhangurbanli.saga.model.event;

import az.tarkhangurbanli.saga.model.enums.EventType;

import java.time.LocalDateTime;

public record OrderCreationFailedEvent(
        String eventId,
        String sagaId,
        EventType eventType,
        String reason,
        LocalDateTime timestamp
) implements SagaEvent {
    public OrderCreationFailedEvent(String eventId, String sagaId, String reason) {
        this(eventId, sagaId, EventType.ORDER_CREATION_FAILED, reason, LocalDateTime.now());
    }
}
