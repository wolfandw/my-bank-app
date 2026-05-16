package io.github.wolfandw.chassis.configuration;

import io.github.wolfandw.chassis.model.Outbox;
import io.github.wolfandw.chassis.repository.OutboxRepository;
import io.github.wolfandw.chassis.service.OutboxProcessorService;
import io.github.wolfandw.chassis.service.OutboxSchedulerService;
import io.github.wolfandw.chassis.service.impl.OutboxProcessorServiceImpl;
import io.github.wolfandw.chassis.service.impl.OutboxSchedulerServiceImpl;
import io.micrometer.core.instrument.Counter;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import reactor.kafka.sender.KafkaSender;

import java.util.UUID;

/**
 * Авто-конфигурация обработки исходящих сообщений.
 */
@AutoConfiguration
@PropertySource("classpath:library.properties")
public class OutboxProcessorAutoConfiguration {
    @Bean
    public OutboxProcessorService outboxProcessorService(@Lazy KafkaSender<UUID, Outbox> kafkaSender,
                                                         @Value("${spring.kafka.topics.topic}") String topic,
                                                         OutboxRepository outboxRepository,
                                                         Counter sendUnsentOutboxSuccessCounter,
                                                         Counter sendUnsentOutboxFailureCounter) {
        return new OutboxProcessorServiceImpl(kafkaSender,
                topic,
                outboxRepository,
                sendUnsentOutboxSuccessCounter,
                sendUnsentOutboxFailureCounter);
    }

    @Bean
    public OutboxSchedulerService outboxScheduleService(OutboxProcessorService outboxProcessorService,
                                                        ObservationRegistry observationRegistry) {
        return new OutboxSchedulerServiceImpl(outboxProcessorService, observationRegistry);
    }
}
