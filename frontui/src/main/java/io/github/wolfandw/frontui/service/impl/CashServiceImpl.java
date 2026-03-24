package io.github.wolfandw.frontui.service.impl;

import io.github.wolfandw.chassis.dto.AccountPageDto;
import io.github.wolfandw.chassis.dto.CashAction;
import io.github.wolfandw.frontui.service.CashService;
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

    private static final String SCHEME = "http";
    private static final String CASH_PATH = "/cash";
    private static final String VALUE_PARAMETER = "value";
    private static final String ACTION_PARAMETER = "action";

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
    public CashServiceImpl(WebClient loadBalancedWebClient) {
        this.loadBalancedWebClient = loadBalancedWebClient;
    }

    @Override
    public Mono<AccountPageDto> editCash(BigDecimal value, CashAction action) {
        LOG.info("Front UI -> Gateway. Отправка запроса на изменение наличных");
        return loadBalancedWebClient.post()
                .uri(uriBuilder -> buildUri(getUriBuilder(uriBuilder), value, action))
                .retrieve()
                .bodyToMono(AccountPageDto.class);
    }

    private UriBuilder getUriBuilder(UriBuilder uriBuilder) {
        return uriBuilder
                .scheme(SCHEME)
                .host(gatewayHost)
                .port(gatewayPort)
                .path(CASH_PATH);
    }

    private URI buildUri(UriBuilder uriBuilder, BigDecimal value, CashAction action) {
        return uriBuilder
                .queryParam(VALUE_PARAMETER, value)
                .queryParam(ACTION_PARAMETER, action)
                .build();
    }
}
