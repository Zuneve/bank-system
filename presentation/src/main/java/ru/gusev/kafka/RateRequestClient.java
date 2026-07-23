package ru.gusev.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.stereotype.Component;
import ru.gusev.exception.InvalidCurrencyException;
import ru.gusev.exception.RateServiceUnavailableException;
import ru.gusev.exception.UnsupportedCurrencyException;
import ru.gusev.request.rate.RateRequestMessage;
import ru.gusev.response.rate.RateResponseMessage;
import ru.gusev.update.RateUpdateMessage;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class RateRequestClient {

    private final ReplyingKafkaTemplate<
            String,
            RateRequestMessage,
            RateResponseMessage> kafkaTemplate;

    @Value("${rates.topics.requests}")
    private String requestTopic;

    public RateResponseMessage request(String currency) {
        RateRequestMessage message = new RateRequestMessage(currency);

        ProducerRecord<String, RateRequestMessage> producerRecord = new ProducerRecord<>(
                requestTopic,
                currency,
                message
        );

        try {
            return kafkaTemplate
                    .sendAndReceive(producerRecord)
                    .get()
                    .value();
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();

            throw new RateServiceUnavailableException(
                    "Waiting for rate response was interrupted",
                    interruptedException
            );
        } catch (ExecutionException exception) {
            throw new RateServiceUnavailableException(
                    "Rates service did not respond",
                    exception.getCause()
            );
        }
    }
}
