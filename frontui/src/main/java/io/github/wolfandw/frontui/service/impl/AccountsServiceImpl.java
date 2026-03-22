package io.github.wolfandw.frontui.service.impl;

import io.github.wolfandw.chassis.dto.AccountPageDto;
import io.github.wolfandw.frontui.service.AccountsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    /**
     * Создает сервис.
     *
     * @param gatewayWebClient веб-клиент
     */
    public AccountsServiceImpl(WebClient gatewayWebClient) {
        this.gatewayWebClient = gatewayWebClient;
    }

    @Override
    public Mono<AccountPageDto> getAccount() {
        LOG.info("Пользователь -> Front UI. Отправка запроса на получение данных аккаунта");
        return gatewayWebClient.get()
                .uri("/account")
                .retrieve()
                .bodyToMono(AccountPageDto.class)
                .switchIfEmpty(Mono.error(new RuntimeException("User data not found")));
    }

    @Override
    public Mono<AccountPageDto> editAccount(String name, LocalDate birthdate) {
        LOG.info("Front UI -> Gateway. Отправка запроса на изменение персональных данных");
        return gatewayWebClient.post()
                .uri(uriBuilder -> buildUri(uriBuilder, name, birthdate))
                .retrieve()
                .bodyToMono(AccountPageDto.class);
    }

    private URI buildUri(UriBuilder uriBuilder, String name, LocalDate birthdate) {
        return uriBuilder
                .path("/account")
                .queryParam("name", name)
                .queryParam("birthdate", birthdate)
                .build();
    }
}
