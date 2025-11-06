package az.tarkhangurbanli.saga.model.dto.response;

import az.tarkhangurbanli.saga.model.enums.SagaStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SagaResponse(
        String sagaId,
        String customerId,
        String orderId,
        String productId,
        Integer quantity,
        BigDecimal amount,
        SagaStatus status,
        String failureReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime completedAt,
        List<SagaStepInfo> steps
) {}
