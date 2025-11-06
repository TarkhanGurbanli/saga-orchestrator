package az.tarkhangurbanli.saga.model.command;

import az.tarkhangurbanli.saga.model.enums.CommandType;

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
) implements SagaCommand {
    public RefundPaymentCommand(String commandId, String sagaId, String orderId,
                                String paymentId, BigDecimal amount, String reason) {
        this(commandId, sagaId, CommandType.REFUND_PAYMENT, orderId,
                paymentId, amount, reason, LocalDateTime.now());
    }
}
