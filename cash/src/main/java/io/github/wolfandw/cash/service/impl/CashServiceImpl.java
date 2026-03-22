package io.github.wolfandw.cash.service.impl;

import io.github.wolfandw.chassis.dto.AccountPageDto;
import io.github.wolfandw.chassis.dto.CashAction;
import io.github.wolfandw.cash.service.CashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;

/**
 * Реализация {@link CashService}
 */
@Service
@ImportAutoConfiguration
public class CashServiceImpl implements CashService {
    private static final Logger LOG = LoggerFactory.getLogger(CashServiceImpl.class);

    private final WebClient accountsWebClient;
    private final WebClient notificationsWebClient;

    /**
     * Создает сервис.
     *
     * @param accountsWebClient accounts веб-клиент
     * @param notificationsWebClient notifications веб-клиент
     */
    public CashServiceImpl(WebClient accountsWebClient, WebClient notificationsWebClient) {
        this.accountsWebClient = accountsWebClient;
        this.notificationsWebClient = notificationsWebClient;
    }

    @Override
    public Mono<AccountPageDto> editCash(BigDecimal value, CashAction action) {
        LOG.info("Cash -> Accounts. Отправка запроса на изменение наличных");
        return accountsWebClient.post()
                .uri(uriBuilder -> buildUri(uriBuilder, value, action))
                .retrieve()
                .bodyToMono(AccountPageDto.class).
                flatMap(accountPageDto -> notify(accountPageDto).thenReturn(accountPageDto));
    }

    private Mono<String> notify(AccountPageDto accountPageDto) {
        LOG.info("Accounts -> Notifications. Отправка запроса на нотификацию");
        return notificationsWebClient.post()
                .uri(uriBuilder -> buildUri(uriBuilder))
                .bodyValue(accountPageDto)
                .retrieve()
                .bodyToMono(String.class);
    }

    private URI buildUri(UriBuilder uriBuilder) {
        return uriBuilder
                .path("/api/notify")
                .build();
    }

    private URI buildUri(UriBuilder uriBuilder, BigDecimal value, CashAction action) {
        return uriBuilder
                .path("/api/cash")
                .queryParam("value", value)
                .queryParam("action", action)
                .build();
    }
}
