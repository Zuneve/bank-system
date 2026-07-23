package ru.gusev.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.metrics.stats.Rate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.gusev.exception.InvalidCurrencyException;
import ru.gusev.exception.UnsupportedCurrencyException;
import ru.gusev.response.rate.RateResponseMessage;
import ru.gusev.update.RateUpdateMessage;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class RateProvider {
    private final RateCache rateCache;
    private final RateRequestClient rateRequestClient;

    @Value("${rates.cache-ttl}")
    private Duration cacheTtlSeconds;

    public RateUpdateMessage getRate(String currency) {
        if (currency.length() != 3) {
            throw new InvalidCurrencyException("Currency length should be 3");
        }

        String normalizeCurrency = normalize(currency);

        if (normalizeCurrency.equals("RUB")) {
            return new RateUpdateMessage("RUB", new BigDecimal(1), Instant.now());
        }

        RateUpdateMessage rateUpdateMessage = rateCache.get(normalizeCurrency);

        if (rateUpdateMessage.timestamp().isAfter(Instant.now().minus(cacheTtlSeconds))) {
            RateResponseMessage response = rateRequestClient.request(normalizeCurrency);

            if (response.error() != null) {
                throw new UnsupportedCurrencyException("Currency not supported");
            }


            RateUpdateMessage updateMessage =
                    new RateUpdateMessage(
                            normalizeCurrency,
                            response.rateToRub(),
                            response.timestamp()
                    );

            rateCache.save(updateMessage);

            return updateMessage ;
        }

        if (rateUpdateMessage.currency() != null) {
            return rateUpdateMessage;
        }

        throw new UnsupportedCurrencyException("Currency not suported");
    }

    private String normalize(String currency) {
        return currency.toUpperCase(Locale.ROOT);
    }
}
