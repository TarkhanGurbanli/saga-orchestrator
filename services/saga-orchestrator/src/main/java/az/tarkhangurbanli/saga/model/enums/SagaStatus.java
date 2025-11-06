package az.tarkhangurbanli.saga.model.enums;

public enum SagaStatus {
    STARTED,
    ORDER_CREATED,
    PAYMENT_PENDING,
    PAYMENT_COMPLETED,
    PAYMENT_FAILED,
    INVENTORY_PENDING,
    INVENTORY_RESERVED,
    INVENTORY_FAILED,
    COMPENSATING,
    COMPLETED,
    FAILED,
    CANCELLED
}
