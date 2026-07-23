package ru.gusev.update;

import java.math.BigDecimal;
import java.time.Instant;

public record RateUpdateMessage(
        String currency,
        BigDecimal rateToRub,
        Instant timestamp
) {
}
