package io.github.wolfandw.chassis.configuration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Авто-конфигурация веб-клиента.
 */
@AutoConfiguration
public class WebClientConfiguration {
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

    /**
     * Load balanced WebClient.
     * Позволяет использовать имена сервисов в URL
     *
     * @return load balanced WebClient
     */
    @Bean
    public WebClient loadBalancedWebClient(WebClient.Builder loadBalancedWebClientBuilder) {
        return loadBalancedWebClientBuilder.build();
    }
}
