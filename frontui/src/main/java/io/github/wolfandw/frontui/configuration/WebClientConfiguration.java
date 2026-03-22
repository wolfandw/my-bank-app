package io.github.wolfandw.frontui.configuration;

import org.springframework.beans.factory.annotation.Value;
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
    public WebClient gatewayWebClient(@Value("${gateway.url}") String gatewayUrl) {
        return WebClient.builder()
                .baseUrl(gatewayUrl)
                .build();
    }
}
