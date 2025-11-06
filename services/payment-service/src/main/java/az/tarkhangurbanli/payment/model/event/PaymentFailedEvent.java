package az.tarkhangurbanli.payment.model.event;

import az.tarkhangurbanli.payment.model.enums.EventType;

import java.time.LocalDateTime;

public record PaymentFailedEvent(
        String eventId,
        String sagaId,
        EventType eventType,
        String orderId,
        String reason,
        LocalDateTime timestamp
) implements PaymentEvent {
    public PaymentFailedEvent(String eventId, String sagaId, String orderId, String reason) {
        this(eventId, sagaId, EventType.PAYMENT_FAILED, orderId, reason, LocalDateTime.now());
    }
}
