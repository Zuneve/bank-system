package ru.gusev.kafka;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.gusev.request.rate.RateRequestMessage;
import ru.gusev.response.rate.RateResponseMessage;
import ru.gusev.update.RateUpdateMessage;

import java.time.Duration;

@Configuration
@EnableKafka
public class BankKafkaConfig {
    @Bean
    public ProducerFactory<String, RateRequestMessage>
    rateRequestProducerFactory(KafkaProperties kafkaProperties) {
        return new DefaultKafkaProducerFactory<>(
                kafkaProperties.buildProducerProperties(),
                new StringSerializer(),
                new JsonSerializer<>()
        );
    }

    @Bean
    public ConsumerFactory<String, RateResponseMessage>
    rateResponseConsumerFactory(KafkaProperties kafkaProperties) {
        return new DefaultKafkaConsumerFactory<>(
                kafkaProperties.buildConsumerProperties(),
                new StringDeserializer(),
                new JsonDeserializer<>(RateResponseMessage.class, false)
        );
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, RateResponseMessage>
    rateRepliesContainer(
            ConsumerFactory<String, RateResponseMessage> consumerFactory,
            @Value("${rates.topics.replies}") String repliesTopic
    ) {
        ContainerProperties properties =
                new ContainerProperties(repliesTopic);

        properties.setGroupId("bank-rate-replies");

        ConcurrentMessageListenerContainer<String, RateResponseMessage>
                container = new ConcurrentMessageListenerContainer<>(
                        consumerFactory,
                        properties
                );

        container.setAutoStartup(false);

        return container;
    }

    @Bean
    public ReplyingKafkaTemplate<
            String,
            RateRequestMessage,
            RateResponseMessage>
    replyingKafkaTemplate(
            ProducerFactory<String, RateRequestMessage> producerFactory,
            ConcurrentMessageListenerContainer<
                    String,
                    RateResponseMessage> repliesContainer,
            @Value("${rates.reply-timeout}") Duration timeout)
    {
        ReplyingKafkaTemplate<
                String,
                RateRequestMessage,
                RateResponseMessage> template =
                new ReplyingKafkaTemplate<>(
                        producerFactory,
                        repliesContainer
                );

        template.setDefaultReplyTimeout(timeout);

        return template;
    }
}
