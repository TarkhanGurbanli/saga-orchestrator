package az.tarkhangurbanli.saga.model.event;

import az.tarkhangurbanli.saga.model.enums.EventType;

import java.time.LocalDateTime;

public record InventoryReleasedEvent(
        String eventId,
        String sagaId,
        EventType eventType,
        String orderId,
        String productId,
        Integer quantity,
        LocalDateTime timestamp
) implements SagaEvent {
    public InventoryReleasedEvent(String eventId, String sagaId, String orderId,
                                  String productId, Integer quantity) {
        this(eventId, sagaId, EventType.INVENTORY_RELEASED, orderId,
                productId, quantity, LocalDateTime.now());
    }
}
