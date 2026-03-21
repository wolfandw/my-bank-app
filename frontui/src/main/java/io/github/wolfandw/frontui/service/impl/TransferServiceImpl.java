package io.github.wolfandw.frontui.service.impl;

import io.github.wolfandw.frontui.dto.AccountPageDto;
import io.github.wolfandw.frontui.service.AccountsService;
import io.github.wolfandw.frontui.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Реализация {@link TransferService}
 */
@Service
public class TransferServiceImpl implements TransferService {
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
    public TransferServiceImpl(WebClient gatewayWebClient, @Value("${bank.gateway.base-url}") String gatewayBaseUrl) {
        this.gatewayWebClient = gatewayWebClient;
        this.gatewayBaseUrl = gatewayBaseUrl;
    }

    @Override
    public Mono<AccountPageDto> transfer(int value, String login) {
        return Mono.just(accountStub.transfer(value, login));
    }
}
