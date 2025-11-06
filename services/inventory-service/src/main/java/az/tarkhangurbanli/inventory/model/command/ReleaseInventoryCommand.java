package az.tarkhangurbanli.inventory.model.command;

import az.tarkhangurbanli.inventory.model.enums.CommandType;

import java.time.LocalDateTime;

public record ReleaseInventoryCommand(
        String commandId,
        String sagaId,
        CommandType commandType,
        String orderId,
        String productId,
        Integer quantity,
        String reason,
        LocalDateTime timestamp
) implements InventoryCommand {
}
