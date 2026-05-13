package io.github.wolfandw.chassis.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Авто-конфигурация веб-клиента.
 */
@AutoConfiguration
@PropertySource("classpath:library.properties")
public class WebClientConfiguration {
    @Value("${gateway.host}")
    private String gatewayHost;

    @Value("${gateway.port}")
    private String gatewayPort;

    @Bean
    public WebClient webClient(ReactiveClientRegistrationRepository clientRegistrations,
                               ServerOAuth2AuthorizedClientRepository authorizedClients) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction oAuth2Filter =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrations, authorizedClients);
        oAuth2Filter.setDefaultClientRegistrationId("keycloak");
        oAuth2Filter.setDefaultOAuth2AuthorizedClient(true);
        return WebClient.builder()
                .baseUrl(gatewayHost + ":" + gatewayPort)
                .filter(oAuth2Filter)
                .build();
    }
}
