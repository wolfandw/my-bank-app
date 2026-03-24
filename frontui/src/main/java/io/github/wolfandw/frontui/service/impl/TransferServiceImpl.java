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

    private static final String SCHEME = "http";
    private static final String TRANSFER_PATH = "/transfer";
    private static final String VALUE_PARAMETER = "value";
    private static final String LOGIN_PARAMETER = "login";

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
    public TransferServiceImpl(WebClient loadBalancedWebClient) {
        this.loadBalancedWebClient = loadBalancedWebClient;
    }

    @Override
    public Mono<AccountPageDto> transfer(BigDecimal value, String login) {
        LOG.info("Front UI -> Gateway. Отправка запроса на перевод наличных");
        return loadBalancedWebClient.post()
                .uri(uriBuilder -> buildUri(getUriBuilder(uriBuilder), value, login))
                .retrieve()
                .bodyToMono(AccountPageDto.class);
    }

    private UriBuilder getUriBuilder(UriBuilder uriBuilder) {
        return uriBuilder
                .scheme(SCHEME)
                .host(gatewayHost)
                .port(gatewayPort)
                .path(TRANSFER_PATH);
    }

    private URI buildUri(UriBuilder uriBuilder, BigDecimal value, String login) {
        return uriBuilder
                .queryParam(VALUE_PARAMETER, value)
                .queryParam(LOGIN_PARAMETER, login)
                .build();
    }
}
