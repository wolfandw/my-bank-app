package io.github.wolfandw.transfer.service.impl;

import io.github.wolfandw.chassis.dto.AccountPageDto;
import io.github.wolfandw.transfer.service.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final WebClient accountsWebClient;
    private final WebClient notificationsWebClient;

    /**
     * Создает сервис.
     *
     * @param accountsWebClient accounts веб-клиент
     * @param notificationsWebClient notifications веб-клиент
     */
    public TransferServiceImpl(WebClient accountsWebClient, WebClient notificationsWebClient) {
        this.accountsWebClient = accountsWebClient;
        this.notificationsWebClient = notificationsWebClient;
    }

    @Override
    public Mono<AccountPageDto> transfer(BigDecimal value, String login) {
        LOG.info("Transfer -> Accounts. Отправка запроса на перевод наличных");
        return accountsWebClient.post()
                .uri(uriBuilder -> buildUri(uriBuilder, value, login))
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

    private URI buildUri(UriBuilder uriBuilder, BigDecimal value, String login) {
        return uriBuilder
                .path("/api/transfer")
                .queryParam("value", value)
                .queryParam("login", login)
                .build();
    }
}
