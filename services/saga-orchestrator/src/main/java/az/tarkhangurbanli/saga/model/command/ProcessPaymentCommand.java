package az.tarkhangurbanli.saga.model.command;

import az.tarkhangurbanli.saga.model.enums.CommandType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProcessPaymentCommand(
        String commandId,
        String sagaId,
        CommandType commandType,
        String orderId,
        String customerId,
        BigDecimal amount,
        LocalDateTime timestamp
) implements SagaCommand {
    public ProcessPaymentCommand(String commandId, String sagaId, String orderId,
                                 String customerId, BigDecimal amount) {
        this(commandId, sagaId, CommandType.PROCESS_PAYMENT, orderId,
                customerId, amount, LocalDateTime.now());
    }
}

