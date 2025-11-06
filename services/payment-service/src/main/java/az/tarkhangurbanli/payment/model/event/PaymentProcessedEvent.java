package az.tarkhangurbanli.payment.model.event;

import az.tarkhangurbanli.payment.model.enums.EventType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentProcessedEvent(
        String eventId,
        String sagaId,
        EventType eventType,
        String orderId,
        String paymentId,
        BigDecimal amount,
        LocalDateTime timestamp
) implements PaymentEvent {
    public PaymentProcessedEvent(String eventId, String sagaId, String orderId,
                                 String paymentId, BigDecimal amount) {
        this(eventId, sagaId, EventType.PAYMENT_PROCESSED, orderId,
                paymentId, amount, LocalDateTime.now());
    }
}
