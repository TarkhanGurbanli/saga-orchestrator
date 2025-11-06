package az.tarkhangurbanli.orders.service;

import az.tarkhangurbanli.orders.kafka.publisher.EventPublisher;
import az.tarkhangurbanli.orders.model.command.CancelOrderCommand;
import az.tarkhangurbanli.orders.model.command.CreateOrderCommand;
import az.tarkhangurbanli.orders.model.entity.Order;
import az.tarkhangurbanli.orders.model.enums.OrderStatus;
import az.tarkhangurbanli.orders.model.event.OrderCancelledEvent;
import az.tarkhangurbanli.orders.model.event.OrderCreatedEvent;
import az.tarkhangurbanli.orders.model.event.OrderCreationFailedEvent;
import az.tarkhangurbanli.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;

    /**
     * Handle Create Order Command
     */
    @Transactional
    public void handleCreateOrder(CreateOrderCommand command) {
        try {
            String orderId = UUID.randomUUID().toString();

            log.info("📝 Creating order for saga: {}", command.sagaId());

            Order order = new Order(
                    orderId,
                    command.sagaId(),
                    command.customerId(),
                    command.productId(),
                    command.quantity(),
                    command.amount(),
                    OrderStatus.CREATED
            );

            orderRepository.save(order);

            log.info("✅ Order created successfully: {}", orderId);

            // Publish success event
            OrderCreatedEvent event = new OrderCreatedEvent(
                    UUID.randomUUID().toString(),
                    command.sagaId(),
                    orderId,
                    command.customerId(),
                    command.productId(),
                    command.quantity(),
                    command.amount()
            );

            eventPublisher.publishOrderCreatedEvent(event);

        } catch (Exception e) {
            log.error("❌ Failed to create order for saga: {}", command.sagaId(), e);

            // Publish failure event
            OrderCreationFailedEvent event = new OrderCreationFailedEvent(
                    UUID.randomUUID().toString(),
                    command.sagaId(),
                    "Failed to create order: " + e.getMessage()
            );

            eventPublisher.publishOrderCreationFailedEvent(event);
        }
    }

    /**
     * Handle Cancel Order Command (Compensation)
     */
    @Transactional
    public void handleCancelOrder(CancelOrderCommand command) {
        try {
            log.info("🚫 Cancelling order: {} for saga: {}", command.orderId(), command.sagaId());

            Order order = orderRepository.findById(command.orderId())
                    .orElseThrow(() -> new RuntimeException("Order not found: " + command.orderId()));

            order.setStatus(OrderStatus.CANCELLED);
            order.setCancellationReason(command.reason());

            orderRepository.save(order);

            log.info("✅ Order cancelled successfully: {}", command.orderId());

            // Publish event
            OrderCancelledEvent event = new OrderCancelledEvent(
                    UUID.randomUUID().toString(),
                    command.sagaId(),
                    command.orderId()
            );

            eventPublisher.publishOrderCancelledEvent(event);

        } catch (Exception e) {
            log.error("❌ Failed to cancel order: {}", command.orderId(), e);
        }
    }

    /**
     * Update order status (for tracking)
     */
    @Transactional
    public void updateOrderStatus(String orderId, OrderStatus status) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(status);
            orderRepository.save(order);
            log.debug("Order {} status updated to {}", orderId, status);
        });
    }

}
