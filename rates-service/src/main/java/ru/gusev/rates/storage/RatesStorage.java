package ru.gusev.rates.storage;

import org.springframework.stereotype.Component;
import ru.gusev.update.RateUpdateMessage;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RatesStorage {
    private final Map<String, RateUpdateMessage> rates = new ConcurrentHashMap<>();

    public void save(RateUpdateMessage rate) {
        rates.put(normalize(rate.currency()), rate);
    }

    public Optional<RateUpdateMessage> find(String currency) {
        if (currency == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(rates.get(normalize(currency)));
    }

    private String normalize(String currency) {
        return currency.toUpperCase(Locale.ROOT);
    }
}
