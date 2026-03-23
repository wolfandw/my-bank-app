package io.github.wolfandw.chassis.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Авто-конфигурация веб-клиента.
 */
@AutoConfiguration
public class WebClientChassisConfiguration {

    /**
     * Load balanced WebClient.Builder.
     * Позволяет использовать имена сервисов в URL
     *
     * @return load balanced WebClient.Builder
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient webClient(WebClient.Builder loadBalancedWebClientBuilder) {

        return loadBalancedWebClientBuilder
                .build();
    }

    /**
     * Регистрирует сконфигурированный веб-клиент для работы с accounts.
     *
     * @return сконфигурированный веб-клиент для работы с accounts
     */
    @Bean
    public WebClient accountsWebClient(WebClient.Builder loadBalancedWebClientBuilder,
                                       @Value("${accounts.url}") String accountsUrl) {
        return loadBalancedWebClientBuilder
                .baseUrl(accountsUrl)
                .build();
    }

    /**
     * Регистрирует сконфигурированный веб-клиент для работы с notifications.
     *
     * @return сконфигурированный веб-клиент для работы с notifications
     */
    @Bean
    public WebClient notificationsWebClient(WebClient.Builder loadBalancedWebClientBuilder,
                                            @Value("${notifications.url}") String notificationsUrl) {
        return loadBalancedWebClientBuilder
                .baseUrl(notificationsUrl)
                .build();
    }

    /**
     * Регистрирует сконфигурированный веб-клиент для работы с gateway.
     *
     * @return сконфигурированный веб-клиент для работы с gateway
     */
    @Bean
    public WebClient gatewayWebClient(WebClient.Builder loadBalancedWebClientBuilder,
                                      @Value("${gateway.url}") String gatewayUrl) {
        return loadBalancedWebClientBuilder
                .baseUrl(gatewayUrl)
                .build();
    }
}
