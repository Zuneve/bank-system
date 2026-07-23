package ru.gusev.response.rate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record RateResponseMessage(
        String currency,
        BigDecimal rateToRub,
        Instant timestamp,
        RateError error
) {
}
