package io.github.wolfandw.frontui.service.impl;

import io.github.wolfandw.frontui.dto.AccountPageDto;
import io.github.wolfandw.frontui.dto.CashAction;
import io.github.wolfandw.frontui.service.CashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Реализация {@link CashService}
 */
@Service
public class CashServiceImpl implements CashService {
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
    public CashServiceImpl(WebClient gatewayWebClient,
                               @Value("${gateway.url}") String gatewayBaseUrl) {
        this.gatewayWebClient = gatewayWebClient;
        this.gatewayBaseUrl = gatewayBaseUrl;
    }

    @Override
    public Mono<AccountPageDto> editCash(int value, CashAction action) {
        return Mono.just(accountStub.editCash(value, action));
    }
}
