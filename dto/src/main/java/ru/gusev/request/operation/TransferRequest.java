package ru.gusev.request.operation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Request for transferring money between accounts")
public record TransferRequest(
        @Schema(description = "Source account identifier", example = "aa027bf2-b5fa-42d8-a34a-9e7c2dc3f8e8")
        @NotNull
        UUID sourceAccountId,
        @Schema(description = "Target account identifier", example = "280037e2-afa0-4e50-968b-0e111157162d")
        @NotNull
        UUID targetAccountId,
        @Schema(description = "Transfer amount before commission", example = "100.00")
        @NotNull
        @Positive
        BigDecimal amount
) {
}
