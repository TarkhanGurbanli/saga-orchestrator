package az.tarkhangurbanli.saga.model.command;

import az.tarkhangurbanli.saga.model.enums.CommandType;

import java.time.LocalDateTime;

public record ReserveInventoryCommand(
        String commandId,
        String sagaId,
        CommandType commandType,
        String orderId,
        String productId,
        Integer quantity,
        LocalDateTime timestamp
) implements SagaCommand {
    public ReserveInventoryCommand(String commandId, String sagaId, String orderId,
                                   String productId, Integer quantity) {
        this(commandId, sagaId, CommandType.RESERVE_INVENTORY, orderId,
                productId, quantity, LocalDateTime.now());
    }
}
