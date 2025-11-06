package az.tarkhangurbanli.payment.model.command;

import az.tarkhangurbanli.payment.model.enums.CommandType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RefundPaymentCommand(
        String commandId,
        String sagaId,
        CommandType commandType,
        String orderId,
        String paymentId,
        BigDecimal amount,
        String reason,
        LocalDateTime timestamp
) implements PaymentCommand {
}
