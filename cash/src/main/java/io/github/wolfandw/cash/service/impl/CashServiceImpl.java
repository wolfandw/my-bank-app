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

    private static final String SCHEME = "http";
    private static final String NOTIFY_PATH = "/api/notify";
    private static final String CASH_PATH = "/api/cash";
    private static final String VALUE_PARAMETER = "value";
    private static final String ACTION_PARAMETER = "action";

    private final WebClient loadBalancedWebClient;

    @Value("${notifications.host}")
    private String notificationsHost;

    @Value("${notifications.port}")
    private String notificationsPort;

    @Value("${accounts.host}")
    private String accountsHost;

    @Value("${accounts.port}")
    private String accountsPort;

    /**
     * Создает сервис.
     *
     * @param loadBalancedWebClient веб-клиент
     */
    public CashServiceImpl(WebClient loadBalancedWebClient) {
        this.loadBalancedWebClient = loadBalancedWebClient;
    }

    @Override
    public Mono<AccountPageDto> editCash(BigDecimal value, CashAction action) {
        LOG.info("Cash -> Accounts. Отправка запроса на изменение наличных");
        return loadBalancedWebClient.post()
                .uri(uriBuilder -> buildUri(uriBuilder, value, action))
                .retrieve()
                .bodyToMono(AccountPageDto.class).
                flatMap(accountPageDto -> notify(accountPageDto).thenReturn(accountPageDto));
    }

    private Mono<String> notify(AccountPageDto accountPageDto) {
        LOG.info("Accounts -> Notifications. Отправка запроса на нотификацию");
        return loadBalancedWebClient.post()
                .uri(this::buildUri)
                .bodyValue(accountPageDto)
                .retrieve()
                .bodyToMono(String.class);
    }

    private URI buildUri(UriBuilder uriBuilder) {
        return uriBuilder
                .scheme(SCHEME)
                .host(notificationsHost)
                .port(notificationsPort)
                .path(NOTIFY_PATH)
                .build();
    }

    private URI buildUri(UriBuilder uriBuilder, BigDecimal value, CashAction action) {
        return uriBuilder
                .scheme(SCHEME)
                .host(accountsHost)
                .port(accountsPort)
                .path(CASH_PATH)
                .queryParam(VALUE_PARAMETER, value)
                .queryParam(ACTION_PARAMETER, action)
                .build();
    }
}
