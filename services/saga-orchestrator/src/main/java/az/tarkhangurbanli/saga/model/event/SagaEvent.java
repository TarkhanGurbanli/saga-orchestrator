package az.tarkhangurbanli.saga.model.event;

import az.tarkhangurbanli.saga.model.enums.EventType;

import java.time.LocalDateTime;

public sealed interface SagaEvent permits
        OrderCreatedEvent,
        OrderCreationFailedEvent,
        PaymentProcessedEvent,
        PaymentFailedEvent,
        InventoryReservedEvent,
        InventoryFailedEvent,
        OrderCancelledEvent,
        PaymentRefundedEvent,
        InventoryReleasedEvent {

    String eventId();

    String sagaId();

    EventType eventType();

    LocalDateTime timestamp();
}
