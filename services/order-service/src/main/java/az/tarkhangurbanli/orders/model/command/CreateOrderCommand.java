package az.tarkhangurbanli.orders.model.command;

import az.tarkhangurbanli.orders.model.enums.CommandType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateOrderCommand(
        String commandId,
        String sagaId,
        CommandType commandType,
        String customerId,
        String productId,
        Integer quantity,
        BigDecimal amount,
        LocalDateTime timestamp
) implements OrderCommand {

}
