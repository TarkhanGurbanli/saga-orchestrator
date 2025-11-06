package az.tarkhangurbanli.inventory.model.event;

import az.tarkhangurbanli.inventory.model.enums.EventType;

import java.time.LocalDateTime;

public record InventoryFailedEvent(
        String eventId,
        String sagaId,
        EventType eventType,
        String orderId,
        String productId,
        String reason,
        LocalDateTime timestamp
) implements InventoryEvent {
    public InventoryFailedEvent(String eventId, String sagaId, String orderId,
                                String productId, String reason) {
        this(eventId, sagaId, EventType.INVENTORY_FAILED, orderId,
                productId, reason, LocalDateTime.now());
    }
}
