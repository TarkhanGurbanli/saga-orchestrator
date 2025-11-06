package az.tarkhangurbanli.payment.model.event;

import az.tarkhangurbanli.payment.model.enums.EventType;

import java.time.LocalDateTime;

public sealed interface PaymentEvent permits PaymentProcessedEvent, PaymentFailedEvent, PaymentRefundedEvent {
    String eventId();

    String sagaId();

    EventType eventType();

    LocalDateTime timestamp();
}
