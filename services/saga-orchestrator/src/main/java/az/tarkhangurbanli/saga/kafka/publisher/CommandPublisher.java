package az.tarkhangurbanli.saga.kafka.publisher;

import az.tarkhangurbanli.saga.model.command.CancelOrderCommand;
import az.tarkhangurbanli.saga.model.command.CreateOrderCommand;
import az.tarkhangurbanli.saga.model.command.ProcessPaymentCommand;
import az.tarkhangurbanli.saga.model.command.RefundPaymentCommand;
import az.tarkhangurbanli.saga.model.command.ReleaseInventoryCommand;
import az.tarkhangurbanli.saga.model.command.ReserveInventoryCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String ORDER_COMMANDS = "order-commands";
    private static final String PAYMENT_COMMANDS = "payment-commands";
    private static final String INVENTORY_COMMANDS = "inventory-commands";

    public void publishCreateOrderCommand(CreateOrderCommand command) {
        publishCommand(ORDER_COMMANDS, command.sagaId(), command);
    }

    public void publishCancelOrderCommand(CancelOrderCommand command) {
        publishCommand(ORDER_COMMANDS, command.sagaId(), command);
    }

    public void publishProcessPaymentCommand(ProcessPaymentCommand command) {
        publishCommand(PAYMENT_COMMANDS, command.sagaId(), command);
    }

    public void publishRefundPaymentCommand(RefundPaymentCommand command) {
        publishCommand(PAYMENT_COMMANDS, command.sagaId(), command);
    }

    public void publishReserveInventoryCommand(ReserveInventoryCommand command) {
        publishCommand(INVENTORY_COMMANDS, command.sagaId(), command);
    }

    public void publishReleaseInventoryCommand(ReleaseInventoryCommand command) {
        publishCommand(INVENTORY_COMMANDS, command.sagaId(), command);
    }

    private void publishCommand(String topic, String key, Object command) {
        try {
            String payload = objectMapper.writeValueAsString(command);

            kafkaTemplate.send(topic, key, payload)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("📤 Command published to {}: {}", topic, command.getClass().getSimpleName());
                        } else {
                            log.error("❌ Failed to publish command to {}", topic, ex);
                        }
                    });

        } catch (Exception e) {
            log.error("Error publishing command", e);
            throw new RuntimeException("Failed to publish command", e);
        }
    }

}
