package az.tarkhangurbanli.inventory.model.event;

import az.tarkhangurbanli.inventory.model.enums.EventType;

import java.time.LocalDateTime;

public sealed interface InventoryEvent permits InventoryReservedEvent, InventoryFailedEvent, InventoryReleasedEvent {
    String eventId();

    String sagaId();

    EventType eventType();

    LocalDateTime timestamp();
}
