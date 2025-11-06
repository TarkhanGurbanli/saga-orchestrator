package az.tarkhangurbanli.saga.model.command;

import az.tarkhangurbanli.saga.model.enums.CommandType;

import java.time.LocalDateTime;

public sealed interface SagaCommand permits
        CreateOrderCommand,
        ProcessPaymentCommand,
        ReserveInventoryCommand,
        CancelOrderCommand,
        RefundPaymentCommand,
        ReleaseInventoryCommand {

    String commandId();

    String sagaId();

    CommandType commandType();

    LocalDateTime timestamp();
}
