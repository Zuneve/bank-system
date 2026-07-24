package ru.gusev.kafka;

import org.junit.jupiter.api.Test;
import ru.gusev.update.RateUpdateMessage;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class RateCacheTest {
    private final RateCache rateCache = new RateCache();

    @Test
    void rateShouldBeSaved() {
        RateUpdateMessage rate = createUsdRate();

        rateCache.save(rate);

        assertNotNull(rateCache.get("USD"));
    }

    @Test
    void savedRateShouldBeReturned() {
        RateUpdateMessage rate = createUsdRate();

        rateCache.save(rate);

        assertEquals(rate, rateCache.get("USD"));
    }

    @Test
    void unknownCurrencyShouldReturnNull() {
        assertNull(rateCache.get("EUR"));
    }

    private RateUpdateMessage createUsdRate() {
        return new RateUpdateMessage(
                "USD",
                new BigDecimal("91.50"),
                Instant.parse("2026-07-23T12:00:00Z")
        );
    }
}
