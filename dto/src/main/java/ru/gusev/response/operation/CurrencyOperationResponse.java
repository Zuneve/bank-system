package ru.gusev.response.operation;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.gusev.operation.OperationType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Account operation with an amount converted from rubles to the requested currency")
public record CurrencyOperationResponse(
        @Schema(description = "Operation identifier", example = "2cf06c93-b72f-44d8-a217-fc26629c9df1")
        UUID id,
        @Schema(description = "Operation type", example = "DEPOSIT")
        OperationType type,
        @Schema(description = "Original operation amount in Russian rubles", example = "9150.00")
        BigDecimal amountRub,
        @Schema(description = "Currency used for the converted amount", example = "USD")
        String currency,
        @Schema(description = "Operation amount in the requested currency", example = "100.00")
        BigDecimal amount,
        @Schema(description = "Rate used for conversion to the requested currency", example = "91.50")
        BigDecimal rateToRub,
        @Schema(description = "Date and time when the conversion rate was generated", example = "2026-07-23T12:00:00Z")
        Instant rateTimestamp,
        @Schema(description = "Date and time when the operation was created", example = "2026-07-23T12:05:00")
        LocalDateTime createdAt,
        @Schema(description = "Identifier of the affected account", example = "e19530c8-8d8b-4f8c-8099-c61fb0dd74fb")
        UUID accountId
) {
}
