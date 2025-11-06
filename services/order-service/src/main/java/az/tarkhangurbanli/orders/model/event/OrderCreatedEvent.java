package az.tarkhangurbanli.orders.model.event;

import az.tarkhangurbanli.orders.model.enums.EventType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderCreatedEvent(
        String eventId,
        String sagaId,
        EventType eventType,
        String orderId,
        String customerId,
        String productId,
        Integer quantity,
        BigDecimal amount,
        LocalDateTime timestamp
) implements OrderEvent {
    public OrderCreatedEvent(String eventId, String sagaId, String orderId,
                             String customerId, String productId, Integer quantity, BigDecimal amount) {
        this(eventId, sagaId, EventType.ORDER_CREATED, orderId, customerId,
                productId, quantity, amount, LocalDateTime.now());
    }
}
