package io.github.wolfandw.frontui.service.impl;

import io.github.wolfandw.chassis.dto.AccountPageDto;
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

    private static final String SCHEME = "http";
    private static final String ACCOUNT_PATH = "/account";
    private static final String NAME_PARAMETER = "name";
    private static final String BIRTHDATE_PARAMETER = "birthdate";

    private final WebClient loadBalancedWebClient;

    @Value("${gateway.host}")
    private String gatewayHost;

    @Value("${gateway.port}")
    private String gatewayPort;

    /**
     * Создает сервис.
     *
     * @param loadBalancedWebClient веб-клиент
     */
    public AccountsServiceImpl(WebClient loadBalancedWebClient) {
        this.loadBalancedWebClient = loadBalancedWebClient;
    }

    @Override
    public Mono<AccountPageDto> getAccount() {
        LOG.info("Пользователь -> Front UI. Отправка запроса на получение данных аккаунта");
        return loadBalancedWebClient.get()
                .uri(uriBuilder -> getUriBuilder(uriBuilder).build())
                .retrieve()
                .bodyToMono(AccountPageDto.class)
                .switchIfEmpty(Mono.error(new RuntimeException("User data not found")));
    }

    @Override
    public Mono<AccountPageDto> editAccount(String name, LocalDate birthdate) {
        LOG.info("Front UI -> Gateway. Отправка запроса на изменение персональных данных");
        return loadBalancedWebClient.post()
                .uri(uriBuilder -> buildUri(getUriBuilder(uriBuilder), name, birthdate))
                .retrieve()
                .bodyToMono(AccountPageDto.class);
    }

    private UriBuilder getUriBuilder(UriBuilder uriBuilder) {
        return uriBuilder
                .scheme(SCHEME)
                .host(gatewayHost)
                .port(gatewayPort)
                .path(ACCOUNT_PATH);
    }

    private URI buildUri(UriBuilder uriBuilder, String name, LocalDate birthdate) {
        return uriBuilder
                .queryParam(NAME_PARAMETER, name)
                .queryParam(BIRTHDATE_PARAMETER, birthdate)
                .build();
    }
}
