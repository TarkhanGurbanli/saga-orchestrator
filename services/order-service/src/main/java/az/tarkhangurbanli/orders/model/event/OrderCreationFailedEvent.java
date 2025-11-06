package az.tarkhangurbanli.orders.model.event;

import az.tarkhangurbanli.orders.model.enums.EventType;

import java.time.LocalDateTime;

public record OrderCreationFailedEvent(
        String eventId,
        String sagaId,
        EventType eventType,
        String reason,
        LocalDateTime timestamp
) implements OrderEvent {
    public OrderCreationFailedEvent(String eventId, String sagaId, String reason) {
        this(eventId, sagaId, EventType.ORDER_CREATION_FAILED, reason, LocalDateTime.now());
    }
}
