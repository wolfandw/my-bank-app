package io.github.wolfandw.chassis.configuration;

import io.github.wolfandw.chassis.repository.OutboxRepository;
import io.github.wolfandw.chassis.service.OutboxSchedulerService;
import io.github.wolfandw.chassis.service.impl.OutboxSchedulerServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Авто-конфигурация обработки исходящих сообщений.
 */
@AutoConfiguration
public class OutboxSchedulerAutoConfiguration {
    @Bean
    public OutboxSchedulerService outboxScheduleService(WebClient scheduleWebClient, OutboxRepository outboxRepository) {
        return new OutboxSchedulerServiceImpl(scheduleWebClient, outboxRepository);
    }
}
