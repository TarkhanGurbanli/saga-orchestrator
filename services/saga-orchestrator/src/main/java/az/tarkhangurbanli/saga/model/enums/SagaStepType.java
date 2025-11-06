package az.tarkhangurbanli.saga.model.enums;

public enum SagaStepType {
    CREATE_ORDER,
    PROCESS_PAYMENT,
    RESERVE_INVENTORY,
    SEND_NOTIFICATION,
    COMPENSATE_ORDER,
    COMPENSATE_PAYMENT,
    COMPENSATE_INVENTORY
}
