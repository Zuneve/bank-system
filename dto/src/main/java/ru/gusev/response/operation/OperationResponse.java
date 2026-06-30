package ru.gusev.response.operation;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.gusev.operation.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Account operation data")
public record OperationResponse(
        @Schema(description = "Operation identifier", example = "2cf06c93-b72f-44d8-a217-fc26629c9df1")
        UUID id,
        @Schema(description = "Operation type", example = "DEPOSIT")
        OperationType type,
        @Schema(description = "Operation amount", example = "100.00")
        BigDecimal amount,
        @Schema(description = "Date and time when the operation was created", example = "2026-06-27T16:30:00")
        LocalDateTime createdAt,
        @Schema(description = "Identifier of the affected account", example = "e19530c8-8d8b-4f8c-8099-c61fb0dd74fb")
        UUID accountId
) {
}
