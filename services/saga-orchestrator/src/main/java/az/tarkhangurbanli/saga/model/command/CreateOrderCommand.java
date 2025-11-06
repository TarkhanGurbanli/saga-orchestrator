package az.tarkhangurbanli.saga.model.command;

import az.tarkhangurbanli.saga.model.enums.CommandType;

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
) implements SagaCommand {
    public CreateOrderCommand(String commandId, String sagaId, String customerId,
                              String productId, Integer quantity, BigDecimal amount) {
        this(commandId, sagaId, CommandType.CREATE_ORDER, customerId, productId,
                quantity, amount, LocalDateTime.now());
    }
}

