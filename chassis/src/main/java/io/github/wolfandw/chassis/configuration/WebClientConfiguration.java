package io.github.wolfandw.chassis.configuration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Авто-конфигурация веб-клиента.
 */
@AutoConfiguration
public class WebClientConfiguration {
    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient webClient(WebClient.Builder loadBalancedWebClientBuilder,
                               ReactiveClientRegistrationRepository clientRegistrations,
                               ServerOAuth2AuthorizedClientRepository authorizedClients) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction oAuth2Filter =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrations, authorizedClients);
        oAuth2Filter.setDefaultClientRegistrationId("keycloak");
        oAuth2Filter.setDefaultOAuth2AuthorizedClient(true);
        return loadBalancedWebClientBuilder
                .filter(oAuth2Filter)
                .build();
    }

    @Bean
    public ReactiveOAuth2AuthorizedClientManager scheduleAuthorizedClientManager(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ReactiveOAuth2AuthorizedClientService authorizedClientService) {

        AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientService);

        ReactiveOAuth2AuthorizedClientProvider authorizedClientProvider = ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .refreshToken()
                .build();

        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        return authorizedClientManager;
    }

    @Bean
    public WebClient scheduleWebClient(ReactiveOAuth2AuthorizedClientManager scheduleAuthorizedClientManager) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction oAuth2Filter =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(scheduleAuthorizedClientManager);
        oAuth2Filter.setDefaultClientRegistrationId("keycloak");
        oAuth2Filter.setDefaultOAuth2AuthorizedClient(true);
        return WebClient.builder()
                .filter(oAuth2Filter)
                .build();
    }
}
