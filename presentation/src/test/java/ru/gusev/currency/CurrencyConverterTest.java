package ru.gusev.currency;

import org.junit.jupiter.api.Test;
import ru.gusev.update.RateUpdateMessage;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CurrencyConverterTest {
    private final CurrencyConverter currencyConverter = new CurrencyConverter();

    @Test
    void rublesShouldBeConvertedToUsd() {
        RateUpdateMessage usdRate = createRate("USD", "67.67");

        BigDecimal result = currencyConverter.fromRub(
                new BigDecimal("6767.00"),
                usdRate
        );

        assertEquals(new BigDecimal("100.00"), result);
    }

    @Test
    void rublesShouldNotBeConvertedForRubCurrency() {
        RateUpdateMessage rubRate = createRate("RUB", "1.00");

        BigDecimal result = currencyConverter.fromRub(
                new BigDecimal("123.45"),
                rubRate
        );

        assertEquals(new BigDecimal("123.45"), result);
    }

    @Test
    void resultShouldBeRoundedToTwoDecimalPlaces() {
        RateUpdateMessage usdRate = createRate("USD", "3.00");

        BigDecimal result = currencyConverter.fromRub(
                new BigDecimal("100.00"),
                usdRate
        );

        assertEquals(new BigDecimal("33.33"), result);
    }

    private RateUpdateMessage createRate(String currency, String rateToRub) {
        return new RateUpdateMessage(
                currency,
                new BigDecimal(rateToRub),
                Instant.parse("2026-07-23T12:00:00Z")
        );
    }
}
