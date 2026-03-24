package io.github.wolfandw.transfer.service.impl;

import io.github.wolfandw.chassis.dto.AccountPageDto;
import io.github.wolfandw.transfer.service.TransferService;
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
    private static final String NOTIFY_PATH = "/api/notify";
    private static final String TRANSFER_PATH = "/api/transfer";
    private static final String VALUE_PARAMETER = "value";
    private static final String LOGIN_PARAMETER = "login";

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
    public TransferServiceImpl(WebClient loadBalancedWebClient) {
        this.loadBalancedWebClient = loadBalancedWebClient;
    }

    @Override
    public Mono<AccountPageDto> transfer(BigDecimal value, String login) {
        LOG.info("Transfer -> Accounts. Отправка запроса на перевод наличных");
        return loadBalancedWebClient.post()
                .uri(uriBuilder -> buildUri(uriBuilder, value, login))
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

    private URI buildUri(UriBuilder uriBuilder, BigDecimal value, String login) {
        return uriBuilder
                .scheme(SCHEME)
                .host(accountsHost)
                .port(accountsPort)
                .path(TRANSFER_PATH)
                .queryParam(VALUE_PARAMETER, value)
                .queryParam(LOGIN_PARAMETER, login)
                .build();
    }
}
