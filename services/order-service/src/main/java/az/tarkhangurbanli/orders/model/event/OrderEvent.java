package az.tarkhangurbanli.orders.model.event;

import az.tarkhangurbanli.orders.model.enums.EventType;

import java.time.LocalDateTime;

public sealed interface OrderEvent permits OrderCreatedEvent, OrderCreationFailedEvent, OrderCancelledEvent {
    String eventId();

    String sagaId();

    EventType eventType();

    LocalDateTime timestamp();
}
