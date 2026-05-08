package io.github.wolfandw.chassis.configuration;

import io.github.wolfandw.chassis.model.Outbox;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Авто-конфигурация продюсера Kafka.
 */
@AutoConfiguration
@PropertySource("classpath:library.properties")
public class KafkaProducerAutoConfiguration {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public SenderOptions<UUID, Outbox> senderOptions(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildProducerProperties());
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, UUIDSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        return SenderOptions.create(props);
    }

    @Bean
    @Lazy
    public KafkaSender<UUID, Outbox> kafkaSender(SenderOptions<UUID, Outbox> senderOptions) {
        return KafkaSender.create(senderOptions);
    }

    @Bean
    public KafkaAdmin.NewTopics topics(
            @Value("${accounts.kafka.topic}") String accountsTopic,
            @Value("${cash.kafka.topic}") String cashTopic,
            @Value("${transfer.kafka.topic}") String transferTopic,
            @Value("${spring.kafka.topics.partitions}") int partitions,
            @Value("${spring.kafka.topics.replicas}") short replicas) {
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name(accountsTopic).partitions(partitions).replicas(replicas).build(),
                TopicBuilder.name(cashTopic).partitions(partitions).replicas(replicas).build(),
                TopicBuilder.name(transferTopic).partitions(partitions).replicas(replicas).build());
    }
}
