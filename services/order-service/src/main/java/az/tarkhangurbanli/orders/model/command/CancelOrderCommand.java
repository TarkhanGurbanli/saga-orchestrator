package az.tarkhangurbanli.orders.model.command;

import az.tarkhangurbanli.orders.model.enums.CommandType;

import java.time.LocalDateTime;

public record CancelOrderCommand(
        String commandId,
        String sagaId,
        CommandType commandType,
        String orderId,
        String reason,
        LocalDateTime timestamp
) implements OrderCommand {

}
