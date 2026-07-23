package ru.gusev.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.gusev.update.RateUpdateMessage;

@Component
@RequiredArgsConstructor
public class RateUpdatesListener {
    private final RateCache rateCache;

    @KafkaListener(
            topics = "${rates.topics.updates}",
            groupId = "bank-rates"
    )
    public void listen(RateUpdateMessage rate) {
        rateCache.save(rate);
        System.out.println("Received rate: " + rate);
    }

}
