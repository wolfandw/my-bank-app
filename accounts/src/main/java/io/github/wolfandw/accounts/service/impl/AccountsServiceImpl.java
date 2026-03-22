package io.github.wolfandw.accounts.service.impl;

import io.github.wolfandw.accounts.service.AccountsService;
import io.github.wolfandw.chassis.dto.AccountPageDto;
import io.github.wolfandw.chassis.dto.CashAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;

/**
 * Реализация {@link AccountsService}
 */
@Service
public class AccountsServiceImpl implements AccountsService {
    private static final Logger LOG = LoggerFactory.getLogger(AccountsServiceImpl.class);

    private final WebClient notificationsWebClient;

    @Autowired
    private AccountStub accountStub;

    /**
     * Создает сервис.
     *
     * @param notificationsWebClient notifications веб-клиент
     */
    public AccountsServiceImpl(WebClient notificationsWebClient) {
        this.notificationsWebClient = notificationsWebClient;
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
}
