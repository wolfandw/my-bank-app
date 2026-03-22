package io.github.wolfandw.transfer.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Конфигурация веб-клиента.
 */
@Configuration
public class WebClientConfiguration {
    /**
     * Регистрирует сконфигурированный веб-клиент.
     *
     * @return сконфигурированный веб-клиент.
     */
    @Bean
    public WebClient gatewayWebClient() {
        return WebClient.builder()
                .build();
    }
}
