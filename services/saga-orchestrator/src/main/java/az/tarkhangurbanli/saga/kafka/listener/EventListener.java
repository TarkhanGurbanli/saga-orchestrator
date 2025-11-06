package az.tarkhangurbanli.saga.kafka.listener;

import az.tarkhangurbanli.saga.model.event.InventoryFailedEvent;
import az.tarkhangurbanli.saga.model.event.InventoryReleasedEvent;
import az.tarkhangurbanli.saga.model.event.InventoryReservedEvent;
import az.tarkhangurbanli.saga.model.event.OrderCancelledEvent;
import az.tarkhangurbanli.saga.model.event.OrderCreatedEvent;
import az.tarkhangurbanli.saga.model.event.OrderCreationFailedEvent;
import az.tarkhangurbanli.saga.model.event.PaymentFailedEvent;
import az.tarkhangurbanli.saga.model.event.PaymentProcessedEvent;
import az.tarkhangurbanli.saga.model.event.PaymentRefundedEvent;
import az.tarkhangurbanli.saga.orchestrator.SagaOrchestrator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventListener {

    private final SagaOrchestrator sagaOrchestrator;
    private final ObjectMapper objectMapper;

    /**
     * Listen to Order events
     */
    @KafkaListener(topics = "order-events", groupId = "saga-orchestrator-group")
    public void handleOrderEvents(@Payload String message, Acknowledgment acknowledgment) {
        try {
            log.debug("📨 Received order event");

            var jsonNode = objectMapper.readTree(message);
            String eventType = jsonNode.get("eventType").asText();

            switch (eventType) {
                case "ORDER_CREATED" -> {
                    OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);
                    sagaOrchestrator.handleOrderCreated(event);
                }
                case "ORDER_CANCELLED" -> {
                    OrderCancelledEvent event = objectMapper.readValue(message, OrderCancelledEvent.class);
                    sagaOrchestrator.handleOrderCancelled(event);
                }
                case "ORDER_CREATION_FAILED" -> {
                    OrderCreationFailedEvent event = objectMapper.readValue(message, OrderCreationFailedEvent.class);
                    log.error("Order creation failed: {}", event.reason());
                }
            }

            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Error processing order event", e);
            acknowledgment.acknowledge();
        }
    }

    /**
     * Listen to Payment events
     */
    @KafkaListener(topics = "payment-events", groupId = "saga-orchestrator-group")
    public void handlePaymentEvents(@Payload String message, Acknowledgment acknowledgment) {
        try {
            log.debug("📨 Received payment event");

            var jsonNode = objectMapper.readTree(message);
            String eventType = jsonNode.get("eventType").asText();

            switch (eventType) {
                case "PAYMENT_PROCESSED" -> {
                    PaymentProcessedEvent event = objectMapper.readValue(message, PaymentProcessedEvent.class);
                    sagaOrchestrator.handlePaymentProcessed(event);
                }
                case "PAYMENT_FAILED" -> {
                    PaymentFailedEvent event = objectMapper.readValue(message, PaymentFailedEvent.class);
                    sagaOrchestrator.handlePaymentFailed(event);
                }
                case "PAYMENT_REFUNDED" -> {
                    PaymentRefundedEvent event = objectMapper.readValue(message, PaymentRefundedEvent.class);
                    sagaOrchestrator.handlePaymentRefunded(event);
                }
            }

            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Error processing payment event", e);
            acknowledgment.acknowledge();
        }
    }

    /**
     * Listen to Inventory events
     */
    @KafkaListener(topics = "inventory-events", groupId = "saga-orchestrator-group")
    public void handleInventoryEvents(@Payload String message, Acknowledgment acknowledgment) {
        try {
            log.debug("📨 Received inventory event");

            var jsonNode = objectMapper.readTree(message);
            String eventType = jsonNode.get("eventType").asText();

            switch (eventType) {
                case "INVENTORY_RESERVED" -> {
                    InventoryReservedEvent event = objectMapper.readValue(message, InventoryReservedEvent.class);
                    sagaOrchestrator.handleInventoryReserved(event);
                }
                case "INVENTORY_FAILED" -> {
                    InventoryFailedEvent event = objectMapper.readValue(message, InventoryFailedEvent.class);
                    sagaOrchestrator.handleInventoryFailed(event);
                }
                case "INVENTORY_RELEASED" -> {
                    InventoryReleasedEvent event = objectMapper.readValue(message, InventoryReleasedEvent.class);
                    log.info("Inventory released for saga: {}", event.sagaId());
                }
            }

            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Error processing inventory event", e);
            acknowledgment.acknowledge();
        }
    }

}
