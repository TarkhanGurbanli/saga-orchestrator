package az.tarkhangurbanli.orders.kafka.listener;

import az.tarkhangurbanli.orders.model.command.CancelOrderCommand;
import az.tarkhangurbanli.orders.model.command.CreateOrderCommand;
import az.tarkhangurbanli.orders.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommandHandler {

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-commands", groupId = "order-service-group")
    public void handleCommand(@Payload String message, Acknowledgment ack) {
        try {
            log.debug("📨 Received order command");

            var jsonNode = objectMapper.readTree(message);
            String commandType = jsonNode.get("commandType").asText();

            switch (commandType) {
                case "CREATE_ORDER" -> {
                    CreateOrderCommand cmd = objectMapper.readValue(message, CreateOrderCommand.class);
                    orderService.handleCreateOrder(cmd);
                }
                case "CANCEL_ORDER" -> {
                    CancelOrderCommand cmd = objectMapper.readValue(message, CancelOrderCommand.class);
                    orderService.handleCancelOrder(cmd);
                }
                default -> log.warn("Unknown command type: {}", commandType);
            }

            ack.acknowledge();

        } catch (Exception e) {
            log.error("❌ Error handling order command", e);
            ack.acknowledge();
        }
    }

}