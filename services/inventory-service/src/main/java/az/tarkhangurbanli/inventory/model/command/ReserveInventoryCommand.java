package az.tarkhangurbanli.inventory.model.command;

import az.tarkhangurbanli.inventory.model.enums.CommandType;

import java.time.LocalDateTime;

public record ReserveInventoryCommand(
        String commandId,
        String sagaId,
        CommandType commandType,
        String orderId,
        String productId,
        Integer quantity,
        LocalDateTime timestamp
) implements InventoryCommand {
}
