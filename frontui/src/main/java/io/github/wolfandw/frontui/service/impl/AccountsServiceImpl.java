package io.github.wolfandw.frontui.service.impl;

import io.github.wolfandw.frontui.controller.AccountsController;
import io.github.wolfandw.frontui.dto.AccountPageDto;
import io.github.wolfandw.frontui.service.AccountsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDate;

/**
 * Реализация {@link AccountsService}
 */
@Service
public class AccountsServiceImpl implements AccountsService {
    private static final Logger LOG = LoggerFactory.getLogger(AccountsServiceImpl.class);
    private final WebClient gatewayWebClient;
    private final String gatewayBaseUrl;

    /**
     * Создает сервис.
     *
     * @param gatewayWebClient веб-клиент
     * @param gatewayBaseUrl URL шлюза
     */
    public AccountsServiceImpl(WebClient gatewayWebClient,
                               @Value("${gateway.url}") String gatewayBaseUrl) {
        this.gatewayWebClient = gatewayWebClient;
        this.gatewayBaseUrl = gatewayBaseUrl;
    }

    @Override
    public Mono<AccountPageDto> getAccount() {
        return gatewayWebClient.get()
                .uri(gatewayBaseUrl + "/account")
                .retrieve()
                .bodyToMono(AccountPageDto.class)
                .switchIfEmpty(Mono.error(new RuntimeException("User data not found")));
    }

    @Override
    public Mono<AccountPageDto> editAccount(String name, LocalDate birthdate) {
        LOG.debug("Front UI -> Gateway. Отправка запроса на изменение персональных данных");
        return gatewayWebClient.post()
                .uri(uriBuilder -> buildUri(uriBuilder, name, birthdate))
                .retrieve()
                .bodyToMono(AccountPageDto.class);
    }

    private URI buildUri(UriBuilder uriBuilder, String name, LocalDate birthdate) {
        return uriBuilder
                .scheme("http")
                .host("localhost")
                .port("8081")
                .path("/account")
                .queryParam("name", name)
                .queryParam("birthdate", birthdate)
                .build();
    }
}
