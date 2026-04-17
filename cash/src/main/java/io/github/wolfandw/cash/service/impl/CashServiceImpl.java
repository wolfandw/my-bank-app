package io.github.wolfandw.cash.service.impl;

import io.github.wolfandw.cash.service.CashService;
import io.github.wolfandw.chassis.dto.CashAction;
import io.github.wolfandw.chassis.dto.OperationResultDto;
import io.github.wolfandw.chassis.model.Outbox;
import io.github.wolfandw.chassis.repository.OutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;

/**
 * Реализация {@link CashService}
 */
@Service
public class CashServiceImpl implements CashService {
    private static final Logger LOG = LoggerFactory.getLogger(CashServiceImpl.class);

    private static final String SCHEME = "http";
    private static final String CASH_PATH = "/api/cash";

    private static final String LOGIN_PARAMETER = "login";
    private static final String VALUE_PARAMETER = "value";
    private static final String ACTION_PARAMETER = "value";

    private static final String ACCOUNTS_API_UNAVAILABLE = "Сервис счетов недоступен: %s";

    private final WebClient webClient;
    private final OutboxRepository outboxRepository;

    @Value("${accounts.host}")
    private String accountsHost;

    @Value("${accounts.port}")
    private String accountsPort;

    /**
     * Создает сервис.
     *
     * @param webClient веб-клиент
     * @param outboxRepository репозиторий сообщений
     */
    public CashServiceImpl(WebClient webClient, OutboxRepository outboxRepository) {
        this.webClient = webClient;
        this.outboxRepository = outboxRepository;
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('USER') and hasRole('CASH_WRITE')")
    public Mono<OperationResultDto> changeCash(String login, BigDecimal value, CashAction action) {
        LOG.debug(createMessage(login, "Cash -> Accounts. Отправка запроса на изменение наличных"));
        return webClient.post()
                .uri(uriBuilder -> buildUri(uriBuilder, login, value, action))
                .retrieve()
                .bodyToMono(OperationResultDto.class)
                .flatMap(operationResultDto -> outbox(operationResultDto).thenReturn(operationResultDto))
                .onErrorResume(e -> {
                    String errorMessage = ACCOUNTS_API_UNAVAILABLE.formatted(e.getMessage());
                    LOG.error(errorMessage, e);
                    return Mono.just(new OperationResultDto(new UUID(0, 0), login,false, errorMessage));
                });
    }

    private Mono<Outbox> outbox(OperationResultDto operationResultDto) {
        return outboxRepository.save(createOutbox(operationResultDto.userId(),
                operationResultDto.login(),
                operationResultDto.message()));
    }

    private URI buildUri(UriBuilder uriBuilder, String login, BigDecimal value, CashAction action) {
        return uriBuilder
                .scheme(SCHEME)
                .host(accountsHost)
                .port(accountsPort)
                .path(CASH_PATH)
                .queryParam(LOGIN_PARAMETER, login)
                .queryParam(VALUE_PARAMETER, value)
                .queryParam(ACTION_PARAMETER, action)
                .build();
    }

    private Outbox createOutbox(UUID userId, String login, String message) {
        Outbox outbox = new Outbox();
        outbox.setUserId(userId);
        outbox.setMessage(createMessage(login, message));
        return outbox;
    }

    private String createMessage(String login, String message) {
        return message + ": '" + login + "'";
    }
}
