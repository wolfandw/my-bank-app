package io.github.wolfandw.chassis.configuration;

import io.github.wolfandw.chassis.model.Outbox;
import io.github.wolfandw.chassis.repository.OutboxRepository;
import io.github.wolfandw.chassis.service.OutboxProcessorService;
import io.github.wolfandw.chassis.service.OutboxSchedulerService;
import io.github.wolfandw.chassis.service.impl.OutboxProcessorServiceImpl;
import io.github.wolfandw.chassis.service.impl.OutboxSchedulerServiceImpl;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Авто-конфигурация обработки исходящих сообщений.
 */
@AutoConfiguration
public class OutboxProcessorAutoConfiguration {
    @Bean
    public OutboxProcessorService outboxProcessorService(@Lazy KafkaSender<UUID, Outbox> kafkaSender,
                                                         @Value("${spring.kafka.topics.topic}") String topic,
                                                         OutboxRepository outboxRepository) {
        return new OutboxProcessorServiceImpl(kafkaSender, topic, outboxRepository);
    }

    @Bean
    public OutboxSchedulerService outboxScheduleService(OutboxProcessorService outboxProcessorService) {
        return new OutboxSchedulerServiceImpl(outboxProcessorService);
    }

    @Bean
    public SenderOptions<UUID, Outbox> senderOptions(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildProducerProperties());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.UUIDSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                org.springframework.kafka.support.serializer.JacksonJsonSerializer.class);
        return SenderOptions.create(props);
    }

    @Bean
    @Lazy
    public KafkaSender<UUID, Outbox> kafkaSender(SenderOptions<UUID, Outbox> senderOptions) {
        return KafkaSender.create(senderOptions);
    }

    @Bean
    public KafkaAdmin.NewTopics topics(
            @Value("${spring.kafka.topics.partitions") int partitions,
            @Value("${spring.kafka.topics.replicas") short replicas) {
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name("accounts-to-notifications").partitions(partitions).replicas(replicas).build(),
                TopicBuilder.name("cash-to-notifications").partitions(partitions).replicas(replicas).build(),
                TopicBuilder.name("transfer-to-notifications").partitions(partitions).replicas(replicas).build(),
                TopicBuilder.name("notifications-to-notifications").partitions(partitions).replicas(replicas).build());
    }
}
