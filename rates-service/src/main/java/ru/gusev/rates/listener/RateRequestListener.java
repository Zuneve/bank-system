package ru.gusev.rates.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import ru.gusev.rates.storage.RatesStorage;
import ru.gusev.request.rate.RateRequestMessage;
import ru.gusev.response.rate.RateError;
import ru.gusev.response.rate.RateResponseMessage;
import ru.gusev.update.RateUpdateMessage;

import java.time.Instant;
import java.util.Locale;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RateRequestListener {
    private final RatesStorage ratesStorage;


    @KafkaListener(
            topics = "${rates.topics.requests}",
            groupId = "rates-service"
    )
    @SendTo
    public RateResponseMessage getRate(RateRequestMessage request) {
        if (request.currency().length() != 3) {
            return new RateResponseMessage(
                    null,
                    null,
                    null,
                    new RateError("", ""));
        }

        Optional<RateUpdateMessage> optionalRateUpdateMessage = ratesStorage.find(request.currency());

        if (optionalRateUpdateMessage.isPresent()) {
            var rateUpdateMessage = optionalRateUpdateMessage.get();
            return new RateResponseMessage(
                    rateUpdateMessage.currency(),
                    rateUpdateMessage.rateToRub(),
                    rateUpdateMessage.timestamp(),
                    null);
        }

        return new RateResponseMessage(
                null,
                null,
                null,
                new RateError("CODE>HZ", "")
        );
    }
}
