package az.tarkhangurbanli.saga.model.command;

import az.tarkhangurbanli.saga.model.enums.CommandType;

import java.time.LocalDateTime;

public record CancelOrderCommand(
        String commandId,
        String sagaId,
        CommandType commandType,
        String orderId,
        String reason,
        LocalDateTime timestamp
) implements SagaCommand {
    public CancelOrderCommand(String commandId, String sagaId, String orderId, String reason) {
        this(commandId, sagaId, CommandType.CANCEL_ORDER, orderId, reason, LocalDateTime.now());
    }
}
