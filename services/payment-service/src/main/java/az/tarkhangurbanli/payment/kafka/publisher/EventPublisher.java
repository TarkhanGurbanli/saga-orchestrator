package az.tarkhangurbanli.payment.kafka.publisher;

import az.tarkhangurbanli.payment.model.event.PaymentFailedEvent;
import az.tarkhangurbanli.payment.model.event.PaymentProcessedEvent;
import az.tarkhangurbanli.payment.model.event.PaymentRefundedEvent;
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

    private static final String PAYMENT_EVENTS_TOPIC = "payment-events";

    public void publishPaymentProcessedEvent(PaymentProcessedEvent event) {
        publishEvent(event.sagaId(), event);
    }

    public void publishPaymentFailedEvent(PaymentFailedEvent event) {
        publishEvent(event.sagaId(), event);
    }

    public void publishPaymentRefundedEvent(PaymentRefundedEvent event) {
        publishEvent(event.sagaId(), event);
    }

    private void publishEvent(String key, Object event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(PAYMENT_EVENTS_TOPIC, key, payload)
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
