package ru.gusev.rates.generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import ru.gusev.rates.storage.RatesStorage;
import ru.gusev.update.RateUpdateMessage;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SuppressWarnings("unchecked")
class RateGeneratorTest {
    private static final String UPDATES_TOPIC = "rates.updates";

    private final RatesStorage ratesStorage = new RatesStorage();
    private final KafkaTemplate<String, RateUpdateMessage> kafkaTemplate =
            mock(KafkaTemplate.class);
    private final RateGenerator rateGenerator =
            new RateGenerator(ratesStorage, kafkaTemplate);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                rateGenerator,
                "updatesTopic",
                UPDATES_TOPIC
        );
    }

    @Test
    void usdAndEurRatesShouldBeGeneratedAndSaved() {
        rateGenerator.generateRates();

        RateUpdateMessage usdRate = ratesStorage.find("USD").orElseThrow();
        RateUpdateMessage eurRate = ratesStorage.find("EUR").orElseThrow();

        assertEquals("USD", usdRate.currency());
        assertEquals("EUR", eurRate.currency());
    }

    @Test
    void messageShouldBeSentForEachCurrency() {
        rateGenerator.generateRates();

        RateUpdateMessage usdRate = ratesStorage.find("USD").orElseThrow();
        RateUpdateMessage eurRate = ratesStorage.find("EUR").orElseThrow();

        verify(kafkaTemplate).send(UPDATES_TOPIC, "USD", usdRate);
        verify(kafkaTemplate).send(UPDATES_TOPIC, "EUR", eurRate);
        verify(kafkaTemplate, times(2))
                .send(
                        eq(UPDATES_TOPIC),
                        anyString(),
                        any(RateUpdateMessage.class)
                );
    }

    @Test
    void generatedRatesShouldBeWithinAllowedRange() {
        rateGenerator.generateRates();

        assertRateInRange("USD", "90.59", "92.41");
        assertRateInRange("EUR", "98.21", "100.19");
    }

    private void assertRateInRange(
            String currency,
            String minimum,
            String maximum) {
        BigDecimal rate = ratesStorage.find(currency)
                .orElseThrow()
                .rateToRub();

        assertTrue(rate.compareTo(new BigDecimal(minimum)) >= 0);
        assertTrue(rate.compareTo(new BigDecimal(maximum)) <= 0);
    }
}
