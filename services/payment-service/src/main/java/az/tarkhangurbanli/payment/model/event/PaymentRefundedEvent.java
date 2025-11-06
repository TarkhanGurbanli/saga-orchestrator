package az.tarkhangurbanli.payment.model.event;

import az.tarkhangurbanli.payment.model.enums.EventType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentRefundedEvent(
        String eventId,
        String sagaId,
        EventType eventType,
        String orderId,
        String paymentId,
        BigDecimal amount,
        LocalDateTime timestamp
) implements PaymentEvent {
    public PaymentRefundedEvent(String eventId, String sagaId, String orderId,
                                String paymentId, BigDecimal amount) {
        this(eventId, sagaId, EventType.PAYMENT_REFUNDED, orderId,
                paymentId, amount, LocalDateTime.now());
    }
}
