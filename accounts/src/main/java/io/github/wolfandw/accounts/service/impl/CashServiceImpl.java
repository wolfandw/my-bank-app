package io.github.wolfandw.accounts.service.impl;

import io.github.wolfandw.accounts.dto.AccountPageDto;
import io.github.wolfandw.accounts.dto.CashAction;
import io.github.wolfandw.accounts.service.CashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Реализация {@link CashService}
 */
@Service
public class CashServiceImpl implements CashService {
    private static final Logger LOG = LoggerFactory.getLogger(CashServiceImpl.class);
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
                           @Value("${bank.gateway.base-url}") String gatewayBaseUrl) {
        this.gatewayWebClient = gatewayWebClient;
        this.gatewayBaseUrl = gatewayBaseUrl;
    }

    @Override
    public Mono<AccountPageDto> editCash(BigDecimal value, CashAction action) {
        LOG.debug("Accounts. Обработка запроса на изменение наличных");
        return Mono.just(accountStub.editCash(value.intValue(), action));
    }
}
