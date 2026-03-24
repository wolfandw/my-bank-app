package io.github.wolfandw.accounts.service.impl;

import io.github.wolfandw.chassis.dto.AccountPageDto;
import io.github.wolfandw.accounts.service.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Реализация {@link TransferService}
 */
@Service
public class TransferServiceImpl implements TransferService {
    private static final Logger LOG = LoggerFactory.getLogger(TransferServiceImpl.class);

    private final WebClient loadBalancedWebClient;

    @Autowired
    private AccountStub accountStub;

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
        LOG.info("Accounts. Обработка запроса на перевод наличных");
        return Mono.just(accountStub.transfer(value.intValue(), login));
    }
}
