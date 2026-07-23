package ru.gusev.response.account;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CurrencyBalanceResponse(
        UUID accountId,
        BigDecimal balanceRub,
        String currency,
        BigDecimal balance,
        BigDecimal rateToRub,
        Instant rateTimestamp
) {
}
