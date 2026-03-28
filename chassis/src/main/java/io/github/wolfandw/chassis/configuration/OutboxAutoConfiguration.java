package io.github.wolfandw.chassis.configuration;

import io.github.wolfandw.chassis.repository.OutboxRepository;
import io.github.wolfandw.chassis.service.OutboxService;
import io.github.wolfandw.chassis.service.impl.OutboxServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Авто-конфигурация исходящих сообщений.
 */
@AutoConfiguration
public class OutboxAutoConfiguration {
    /**
     * Создает сервис исходящих сообщений.
     *
     * @param loadBalancedWebClient load-balanced web-client
     * @param outboxRepository репозиторий исходящих сообщений
     * @return сервис исходящих сообщений
     */
    @Bean
    public OutboxService outboxService(WebClient loadBalancedWebClient, OutboxRepository outboxRepository) {
        return new OutboxServiceImpl(loadBalancedWebClient, outboxRepository);
    }
}
