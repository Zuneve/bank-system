package ru.gusev.response.account;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Account balance converted from rubles to the requested currency")
public record CurrencyBalanceResponse(
        @Schema(description = "Account identifier", example = "e19530c8-8d8b-4f8c-8099-c61fb0dd74fb")
        UUID accountId,
        @Schema(description = "Original account balance in Russian rubles", example = "9150.00")
        BigDecimal balanceRub,
        @Schema(description = "Currency used for the converted balance", example = "USD")
        String currency,
        @Schema(description = "Account balance in the requested currency", example = "100.00")
        BigDecimal balance,
        @Schema(description = "Rate used for conversion to the requested currency", example = "91.50")
        BigDecimal rateToRub,
        @Schema(description = "Date and time when the conversion rate was generated", example = "2026-07-23T12:00:00Z")
        Instant rateTimestamp
) {
}
