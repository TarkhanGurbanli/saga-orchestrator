package az.tarkhangurbanli.orders.model.event;

import az.tarkhangurbanli.orders.model.enums.EventType;

import java.time.LocalDateTime;

public record OrderCancelledEvent(
        String eventId,
        String sagaId,
        EventType eventType,
        String orderId,
        LocalDateTime timestamp
) implements OrderEvent {
    public OrderCancelledEvent(String eventId, String sagaId, String orderId) {
        this(eventId, sagaId, EventType.ORDER_CANCELLED, orderId, LocalDateTime.now());
    }
}
