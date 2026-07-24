package ru.gusev.kafka;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.gusev.exception.InvalidCurrencyException;
import ru.gusev.exception.RateServiceUnavailableException;
import ru.gusev.exception.UnsupportedCurrencyException;
import ru.gusev.response.rate.RateError;
import ru.gusev.response.rate.RateResponseMessage;
import ru.gusev.update.RateUpdateMessage;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class RateProviderTest {
    private final RateCache rateCache = new RateCache();
    private final RateRequestClient rateRequestClient = mock(RateRequestClient.class);
    private final RateProvider rateProvider = new RateProvider(rateCache, rateRequestClient);

    @Test
    void RateToRubShouldBeOneWhenCurrencyIsRub() {
        assertEquals(new BigDecimal(1), rateProvider.getRate("RUB").rateToRub());
    }

    @Test
    void freshRateShouldBeReturnedFromCache() {
        RateUpdateMessage cachedRate = new RateUpdateMessage(
                "USD",
                new BigDecimal("91.50"),
                Instant.now().minusSeconds(5)
        );
        rateCache.save(cachedRate);

        ReflectionTestUtils.setField(
                rateProvider,
                "cacheTtlSeconds",
                Duration.ofSeconds(30)
        );

        RateUpdateMessage result = rateProvider.getRate("USD");

        assertEquals(cachedRate, result);
        verify(rateRequestClient, never()).request("USD");
    }

    @Test
    void rateShouldBeRequestedWhenCacheIsEmpty() {
        Instant timestamp = Instant.parse("2026-07-24T12:00:00Z");

        RateResponseMessage response = new RateResponseMessage(
                "USD",
                new BigDecimal("91.50"),
                timestamp,
                null
        );

        when(rateRequestClient.request("USD"))
                .thenReturn(response);

        ReflectionTestUtils.setField(
                rateProvider,
                "cacheTtlSeconds",
                Duration.ofSeconds(30)
        );

        RateUpdateMessage result = rateProvider.getRate("USD");

        verify(rateRequestClient).request("USD");
        assertEquals("USD", result.currency());
        assertEquals(new BigDecimal("91.50"), result.rateToRub());
        assertEquals(timestamp, result.timestamp());
    }

    @Test
    void requestedRateShouldBeSavedInCache() {
        Instant timestamp = Instant.parse("2026-07-24T12:00:00Z");
        RateResponseMessage response = new RateResponseMessage(
                "USD",
                new BigDecimal("91.50"),
                timestamp,
                null
        );
        when(rateRequestClient.request("USD")).thenReturn(response);

        RateUpdateMessage result = rateProvider.getRate("USD");

        assertEquals(result, rateCache.get("USD"));
    }

    @Test
    void invalidCurrencyCodeShouldThrowException() {
        assertThrows(
                InvalidCurrencyException.class,
                () -> rateProvider.getRate("US")
        );

        verifyNoInteractions(rateRequestClient);
    }

    @Test
    void ratesServiceErrorShouldThrowUnsupportedCurrencyException() {
        RateResponseMessage response = new RateResponseMessage(
                null,
                null,
                null,
                new RateError(
                        "CURRENCY_NOT_SUPPORTED",
                        "Currency GBP is not supported"
                )
        );
        when(rateRequestClient.request("GBP")).thenReturn(response);

        assertThrows(
                UnsupportedCurrencyException.class,
                () -> rateProvider.getRate("GBP")
        );
    }

    @Test
    void unavailableRatesServiceShouldThrowException() {
        when(rateRequestClient.request("USD"))
                .thenThrow(new RateServiceUnavailableException(
                        "Rates service did not respond"
                ));

        assertThrows(
                RateServiceUnavailableException.class,
                () -> rateProvider.getRate("USD")
        );
    }
}
