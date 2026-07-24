package ru.gusev.rates.listener;

import org.junit.jupiter.api.Test;
import ru.gusev.rates.storage.RatesStorage;
import ru.gusev.request.rate.RateRequestMessage;
import ru.gusev.response.rate.RateResponseMessage;
import ru.gusev.update.RateUpdateMessage;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class RateRequestListenerTest {
    private final RatesStorage ratesStorage = new RatesStorage();
    private final RateRequestListener listener =
            new RateRequestListener(ratesStorage);

    @Test
    void existingCurrencyShouldReturnRate() {
        RateUpdateMessage rate = new RateUpdateMessage(
                "USD",
                new BigDecimal("91.50"),
                Instant.parse("2026-07-23T12:00:00Z")
        );
        ratesStorage.save(rate);

        RateResponseMessage response =
                listener.getRate(new RateRequestMessage("USD"));

        assertEquals(rate.currency(), response.currency());
        assertEquals(rate.rateToRub(), response.rateToRub());
        assertEquals(rate.timestamp(), response.timestamp());
        assertNull(response.error());
    }

    @Test
    void unknownCurrencyShouldReturnError() {
        RateResponseMessage response =
                listener.getRate(new RateRequestMessage("EUR"));

        assertNotNull(response.error());
        assertNull(response.rateToRub());
    }

    @Test
    void invalidCurrencyCodeShouldReturnError() {
        RateResponseMessage response =
                listener.getRate(new RateRequestMessage("US"));

        assertNotNull(response.error());
        assertNull(response.rateToRub());
    }
}
