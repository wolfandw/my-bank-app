package io.github.wolfandw.chassis.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Авто-конфигурация веб-клиента.
 */
@AutoConfiguration
public class WebClientChassisConfiguration {
    /**
     * Регистрирует сконфигурированный веб-клиент.
     *
     * @return сконфигурированный веб-клиент.
     */
    @Bean
    public WebClient accountsWebClient(@Value("${accounts.url}") String accountsUrl) {
        return WebClient.builder()
                .baseUrl(accountsUrl)
                .build();
    }

    /**
     * Регистрирует сконфигурированный веб-клиент.
     *
     * @return сконфигурированный веб-клиент.
     */
    @Bean
    public WebClient notificationsWebClient(@Value("${notifications.url}") String notificationsUrl) {
        return WebClient.builder()
                .baseUrl(notificationsUrl)
                .build();
    }
}
