package ru.gusev.response.operation;

import ru.gusev.operation.OperationType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record CurrencyOperationResponse(
        UUID id,
        OperationType type,
        BigDecimal amountRub,
        String currency,
        BigDecimal amount,
        BigDecimal rateToRub,
        Instant rateTimestamp,
        LocalDateTime createdAt,
        UUID accountId
) {
}
