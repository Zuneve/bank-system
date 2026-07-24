package ru.gusev.response.rate;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "Kafka response containing a requested currency rate or an error")
public record RateResponseMessage(
        @Schema(description = "Three-letter currency code", example = "USD")
        String currency,
        @Schema(description = "Value of one currency unit in Russian rubles", example = "91.50")
        BigDecimal rateToRub,
        @Schema(description = "Date and time when the rate was generated", example = "2026-07-23T12:00:00Z")
        Instant timestamp,
        @Schema(description = "Rate request error. Null when the request is successful")
        RateError error
) {
}
