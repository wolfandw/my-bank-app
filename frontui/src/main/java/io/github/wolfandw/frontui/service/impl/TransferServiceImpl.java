package io.github.wolfandw.frontui.service.impl;

import io.github.wolfandw.chassis.dto.AccountPageDto;
import io.github.wolfandw.frontui.service.TransferService;
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
 * Реализация {@link TransferService}
 */
@Service
public class TransferServiceImpl implements TransferService {
    private static final Logger LOG = LoggerFactory.getLogger(TransferServiceImpl.class);

    private final WebClient gatewayWebClient;

    /**
     * Создает сервис.
     *
     * @param gatewayWebClient веб-клиент
     */
    public TransferServiceImpl(WebClient gatewayWebClient) {
        this.gatewayWebClient = gatewayWebClient;
    }

    @Override
    public Mono<AccountPageDto> transfer(BigDecimal value, String login) {
        LOG.info("Front UI -> Gateway. Отправка запроса на перевод наличных");
        return gatewayWebClient.post()
                .uri(uriBuilder -> buildUri(uriBuilder, value, login))
                .retrieve()
                .bodyToMono(AccountPageDto.class);
    }

    private URI buildUri(UriBuilder uriBuilder, BigDecimal value, String login) {
        return uriBuilder
                .path("/transfer")
                .queryParam("value", value)
                .queryParam("login", login)
                .build();
    }
}
