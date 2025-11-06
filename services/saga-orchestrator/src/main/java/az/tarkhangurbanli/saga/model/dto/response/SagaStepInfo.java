package az.tarkhangurbanli.saga.model.dto.response;

import java.time.LocalDateTime;

public record SagaStepInfo(
        String stepType,
        String status,
        Integer stepOrder,
        String failureReason,
        LocalDateTime completedAt
) {
}
