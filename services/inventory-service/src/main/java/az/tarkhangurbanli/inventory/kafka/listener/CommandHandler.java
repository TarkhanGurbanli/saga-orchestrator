package az.tarkhangurbanli.inventory.kafka.listener;

import az.tarkhangurbanli.inventory.model.command.ReleaseInventoryCommand;
import az.tarkhangurbanli.inventory.model.command.ReserveInventoryCommand;
import az.tarkhangurbanli.inventory.service.InventoryService;
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

    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "inventory-commands", groupId = "inventory-service-group")
    public void handleCommand(@Payload String message, Acknowledgment ack) {
        try {
            log.debug("Received inventory command");

            var jsonNode = objectMapper.readTree(message);
            String commandType = jsonNode.get("commandType").asText();

            switch (commandType) {
                case "RESERVE_INVENTORY" -> {
                    ReserveInventoryCommand cmd = objectMapper.readValue(message, ReserveInventoryCommand.class);
                    inventoryService.handleReserveInventory(cmd);
                }
                case "RELEASE_INVENTORY" -> {
                    ReleaseInventoryCommand cmd = objectMapper.readValue(message, ReleaseInventoryCommand.class);
                    inventoryService.handleReleaseInventory(cmd);
                }
                default -> log.warn("Unknown command type: {}", commandType);
            }

            ack.acknowledge();

        } catch (Exception e) {
            log.error("Error handling inventory command", e);
            ack.acknowledge();
        }
    }

}