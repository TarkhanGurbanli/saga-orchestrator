package az.tarkhangurbanli.saga.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record StartSagaRequest(
        @NotBlank(message = "Customer ID is required")
        String customerId,

        @NotBlank(message = "Product ID is required")
        String productId,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        Integer quantity,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        BigDecimal amount
) {}
