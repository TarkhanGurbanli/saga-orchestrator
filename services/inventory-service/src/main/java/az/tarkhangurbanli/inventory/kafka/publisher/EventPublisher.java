package az.tarkhangurbanli.inventory.kafka.publisher;

import az.tarkhangurbanli.inventory.model.event.InventoryFailedEvent;
import az.tarkhangurbanli.inventory.model.event.InventoryReleasedEvent;
import az.tarkhangurbanli.inventory.model.event.InventoryReservedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String INVENTORY_EVENTS_TOPIC = "inventory-events";

    public void publishInventoryReservedEvent(InventoryReservedEvent event) {
        publishEvent(event.sagaId(), event);
    }

    public void publishInventoryFailedEvent(InventoryFailedEvent event) {
        publishEvent(event.sagaId(), event);
    }

    public void publishInventoryReleasedEvent(InventoryReleasedEvent event) {
        publishEvent(event.sagaId(), event);
    }

    private void publishEvent(String key, Object event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(INVENTORY_EVENTS_TOPIC, key, payload)
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