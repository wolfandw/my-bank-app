package io.github.wolfandw.accounts.service.impl;

import io.github.wolfandw.accounts.service.AccountsService;
import io.github.wolfandw.chassis.dto.AccountPageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private static final String NOTIFY_PATH = "/api/notify";

    private final WebClient loadBalancedWebClient;

    @Value("${notifications.host}")
    private String notificationsHost;

    @Value("${notifications.port}")
    private String notificationsPort;

    @Autowired
    private AccountStub accountStub;

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
        LOG.info("Accounts. Обработка запроса на получение данных аккаунта");
        return Mono.just(accountStub.fillModel(null, null));
    }

    @Override
    public Mono<AccountPageDto> editAccount(String name, LocalDate birthdate) {
        LOG.info("Accounts. Обработка запроса на изменение персональных данных");
        return Mono.just(accountStub.editAccount(name, birthdate)).
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
}
