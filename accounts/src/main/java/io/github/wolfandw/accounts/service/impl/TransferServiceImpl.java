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

    private final WebClient notificationsWebClient;

    @Autowired
    private AccountStub accountStub;

    /**
     * Создает сервис.
     *
     * @param notificationsWebClient веб-клиент
     */
    public TransferServiceImpl(WebClient notificationsWebClient) {
        this.notificationsWebClient = notificationsWebClient;
    }

    @Override
    public Mono<AccountPageDto> transfer(BigDecimal value, String login) {
        LOG.info("Accounts. Обработка запроса на перевод наличных");
        return Mono.just(accountStub.transfer(value.intValue(), login));
    }
}
