package az.tarkhangurbanli.orders.model.command;

import az.tarkhangurbanli.orders.model.enums.CommandType;

import java.time.LocalDateTime;

public sealed interface OrderCommand permits CreateOrderCommand, CancelOrderCommand {
    String commandId();

    String sagaId();

    CommandType commandType();

    LocalDateTime timestamp();
}
