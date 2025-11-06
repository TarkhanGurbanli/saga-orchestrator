package az.tarkhangurbanli.saga.model.event;

import az.tarkhangurbanli.saga.model.enums.EventType;

import java.time.LocalDateTime;

public record InventoryReservedEvent(
        String eventId,
        String sagaId,
        EventType eventType,
        String orderId,
        String productId,
        Integer quantity,
        LocalDateTime timestamp
) implements SagaEvent {
    public InventoryReservedEvent(String eventId, String sagaId, String orderId,
                                  String productId, Integer quantity) {
        this(eventId, sagaId, EventType.INVENTORY_RESERVED, orderId,
                productId, quantity, LocalDateTime.now());
    }
}
