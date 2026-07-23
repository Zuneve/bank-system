package ru.gusev.rates.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.gusev.rates.storage.RatesStorage;
import ru.gusev.update.RateUpdateMessage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class RateGenerator {
    private static final Map<String, BigDecimal> INITIAL_RATES = Map.of(
            "USD", new BigDecimal("91.50"),
            "EUR", new BigDecimal("99.20")
    );

    private final RatesStorage ratesStorage;
    private final KafkaTemplate<String, RateUpdateMessage> kafkaTemplate;

    @Value("${rates.topics.updates}")
    private String updatesTopic;

    @Scheduled(
            fixedDelayString = "${rates.generation-interval-ms}",
            initialDelayString = "${rates.generation-initial-delay-ms:1000}"
    )
    public void generateRates() {
        INITIAL_RATES.forEach((currency, initialRate) -> {
            BigDecimal currentRate = ratesStorage.find(currency)
                    .map(RateUpdateMessage::rateToRub)
                    .orElse(initialRate);

            BigDecimal multiplier = BigDecimal.valueOf(
                    ThreadLocalRandom.current().nextDouble(0.99, 1.01)
            );
            BigDecimal newRate = currentRate.multiply(multiplier)
                    .setScale(2, RoundingMode.HALF_UP);

            RateUpdateMessage message = new RateUpdateMessage(
                    currency,
                    newRate,
                    Instant.now()
            );
            ratesStorage.save(message);
            kafkaTemplate.send(updatesTopic, currency, message);
        });
    }
}
