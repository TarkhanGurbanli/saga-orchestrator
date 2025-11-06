package az.tarkhangurbanli.payment.model.command;

import az.tarkhangurbanli.payment.model.enums.CommandType;

import java.time.LocalDateTime;

public sealed interface PaymentCommand permits ProcessPaymentCommand, RefundPaymentCommand {
    String commandId();

    String sagaId();

    CommandType commandType();

    LocalDateTime timestamp();
}
