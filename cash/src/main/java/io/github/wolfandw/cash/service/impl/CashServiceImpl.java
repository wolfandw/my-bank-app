package io.github.wolfandw.cash.service.impl;

import io.github.wolfandw.cash.dto.AccountPageDto;
import io.github.wolfandw.cash.dto.CashAction;
import io.github.wolfandw.cash.service.CashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
public class CashServiceImpl implements CashService {
    private static final Logger LOG = LoggerFactory.getLogger(CashServiceImpl.class);
    private final WebClient gatewayWebClient;
    private final String gatewayBaseUrl;

    /**
     * Создает сервис.
     *
     * @param gatewayWebClient веб-клиент
     * @param gatewayBaseUrl URL шлюза
     */
    public CashServiceImpl(WebClient gatewayWebClient,
                               @Value("${bank.accounts.base-url}") String gatewayBaseUrl) {
        this.gatewayWebClient = gatewayWebClient;
        this.gatewayBaseUrl = gatewayBaseUrl;
    }

    @Override
    public Mono<AccountPageDto> editCash(BigDecimal value, CashAction action) {
        LOG.error("Отправка запроса на изменение наличных из Кеш-сервис в Аккаунт-сервис");
        return gatewayWebClient.post()
                .uri(uriBuilder -> buildUri(uriBuilder, value, action))
                .retrieve()
                .bodyToMono(AccountPageDto.class);
    }

    private URI buildUri(UriBuilder uriBuilder, BigDecimal value, CashAction action) {
        return uriBuilder
                .scheme("http")
                .host("localhost")
                .port("8083")
                .path("/api/cash")
                .queryParam("value", value)
                .queryParam("action", action)
                .build();
    }
}
