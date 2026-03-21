package io.github.wolfandw.frontui.service.impl;

import io.github.wolfandw.frontui.dto.AccountPageDto;
import io.github.wolfandw.frontui.service.AccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

/**
 * Реализация {@link AccountsService}
 */
@Service
public class AccountsServiceImpl implements AccountsService {
    private final WebClient gatewayWebClient;
    private final String gatewayBaseUrl;

    @Autowired
    private AccountStub accountStub;

    /**
     * Создает сервис.
     *
     * @param gatewayWebClient веб-клиент
     * @param gatewayBaseUrl URL шлюза
     */
    public AccountsServiceImpl(WebClient gatewayWebClient,
                               @Value("${bank.gateway.base-url}") String gatewayBaseUrl) {
        this.gatewayWebClient = gatewayWebClient;
        this.gatewayBaseUrl = gatewayBaseUrl;
    }

    @Override
    public Mono<AccountPageDto> getAccount() {
        return Mono.just(accountStub.fillModel(null, null));
    }

    @Override
    public Mono<AccountPageDto> editAccount(String name, LocalDate birthdate) {
        return Mono.just(accountStub.editAccount(name, birthdate));
    }
}
