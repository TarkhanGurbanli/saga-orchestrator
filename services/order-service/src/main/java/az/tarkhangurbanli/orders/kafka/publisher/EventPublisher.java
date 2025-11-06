package az.tarkhangurbanli.orders.kafka.publisher;

import az.tarkhangurbanli.orders.model.event.OrderCancelledEvent;
import az.tarkhangurbanli.orders.model.event.OrderCreatedEvent;
import az.tarkhangurbanli.orders.model.event.OrderCreationFailedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String ORDER_EVENTS_TOPIC = "order-events";

    public void publishOrderCreatedEvent(OrderCreatedEvent event) {
        publishEvent(event.sagaId(), event);
    }

    public void publishOrderCreationFailedEvent(OrderCreationFailedEvent event) {
        publishEvent(event.sagaId(), event);
    }

    public void publishOrderCancelledEvent(OrderCancelledEvent event) {
        publishEvent(event.sagaId(), event);
    }

    private void publishEvent(String key, Object event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(ORDER_EVENTS_TOPIC, key, payload)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Event published: {}", event.getClass().getSimpleName());
                        } else {
                            log.error("Failed to publish event", ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing event", e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }
}
