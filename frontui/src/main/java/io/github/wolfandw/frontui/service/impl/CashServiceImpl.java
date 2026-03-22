package io.github.wolfandw.frontui.service.impl;

import io.github.wolfandw.chassis.dto.AccountPageDto;
import io.github.wolfandw.chassis.dto.CashAction;
import io.github.wolfandw.frontui.service.CashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    /**
     * Создает сервис.
     *
     * @param gatewayWebClient веб-клиент
     */
    public CashServiceImpl(WebClient gatewayWebClient) {
        this.gatewayWebClient = gatewayWebClient;
    }

    @Override
    public Mono<AccountPageDto> editCash(BigDecimal value, CashAction action) {
        LOG.info("Front UI -> Gateway. Отправка запроса на изменение наличных");
        return gatewayWebClient.post()
                .uri(uriBuilder -> buildUri(uriBuilder, value, action))
                .retrieve()
                .bodyToMono(AccountPageDto.class);
    }

    private URI buildUri(UriBuilder uriBuilder, BigDecimal value, CashAction action) {
        return uriBuilder
                .path("/cash")
                .queryParam("value", value)
                .queryParam("action", action)
                .build();
    }
}
