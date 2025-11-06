package az.tarkhangurbanli.payment.model.command;

import az.tarkhangurbanli.payment.model.enums.CommandType;

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
) implements PaymentCommand {
}
