package az.tarkhangurbanli.inventory.model.command;

import az.tarkhangurbanli.inventory.model.enums.CommandType;

import java.time.LocalDateTime;

public sealed interface InventoryCommand permits ReserveInventoryCommand, ReleaseInventoryCommand {
    String commandId();

    String sagaId();

    CommandType commandType();

    LocalDateTime timestamp();
}

