package az.tarkhangurbanli.saga.model.command;

import az.tarkhangurbanli.saga.model.enums.CommandType;

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
) implements SagaCommand {
    public ReleaseInventoryCommand(String commandId, String sagaId, String orderId,
                                   String productId, Integer quantity, String reason) {
        this(commandId, sagaId, CommandType.RELEASE_INVENTORY, orderId,
                productId, quantity, reason, LocalDateTime.now());
    }
}
