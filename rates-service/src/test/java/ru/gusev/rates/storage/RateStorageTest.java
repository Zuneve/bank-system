package ru.gusev.rates.storage;

import org.junit.jupiter.api.Test;
import ru.gusev.update.RateUpdateMessage;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class RateStorageTest {
    private final RatesStorage ratesStorage = new RatesStorage();

    @Test
    void rateShouldBeSavedSuccessfully() {
        RateUpdateMessage rateUpdateMessage = new RateUpdateMessage("DJK", new BigDecimal(10), Instant.now());

        ratesStorage.save(rateUpdateMessage);

        assertTrue(ratesStorage.find("DJK").isPresent());

        assertEquals(
                rateUpdateMessage,
                ratesStorage.find("DJK").orElseThrow()
        );
    }

    @Test
    void unsupportedRateNotFound() {
        RateUpdateMessage rateUpdateMessage = new RateUpdateMessage("DJK", new BigDecimal(10), Instant.now());

        ratesStorage.save(rateUpdateMessage);

        assertFalse(ratesStorage.find("DIO").isPresent());
    }

    @Test
    void sameStringInUpperAndLowerCaseIsSameCurrency() {
        RateUpdateMessage rateUpdateMessage = new RateUpdateMessage("por", new BigDecimal(10), Instant.now());

        ratesStorage.save(rateUpdateMessage);

        assertTrue(ratesStorage.find("POR").isPresent());
    }
}
