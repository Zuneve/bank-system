package ru.gusev.kafka;

import org.springframework.stereotype.Component;
import ru.gusev.update.RateUpdateMessage;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateCache {
    private final Map<String, RateUpdateMessage> rates =
            new ConcurrentHashMap<>();

    public void save(RateUpdateMessage rate) {
        rates.put(rate.currency(), rate);
    }

    public RateUpdateMessage get(String currency) {
        return rates.get(currency);
    }
}
