package io.github.wolfandw.transfer.service.impl;

import io.github.wolfandw.chassis.dto.OperationResultDto;
import io.github.wolfandw.chassis.model.Outbox;
import io.github.wolfandw.chassis.repository.OutboxRepository;
import io.github.wolfandw.transfer.service.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;

/**
 * Реализация {@link TransferService}
 */
@Service
public class TransferServiceImpl implements TransferService {
    private static final Logger LOG = LoggerFactory.getLogger(TransferServiceImpl.class);

    private static final String SCHEME = "http";
    private static final String TRANSFER_PATH = "/api/transfer";

    private static final String LOGIN_PARAMETER = "login";
    private static final String VALUE_PARAMETER = "value";
    private static final String RECIPIENT_PARAMETER = "recipient";

    private final String ACCOUNTS_API_UNAVAILABLE = "Сервис счетов недоступен: %s";

    private final WebClient loadBalancedWebClient;
    private final OutboxRepository outboxRepository;

    @Value("${accounts.host}")
    private String accountsHost;

    @Value("${accounts.port}")
    private String accountsPort;

    /**
     * Создает сервис.
     *
     * @param loadBalancedWebClient веб-клиент
     * @param outboxRepository репозиторий сообщений
     */
    public TransferServiceImpl(WebClient loadBalancedWebClient, OutboxRepository outboxRepository) {
        this.loadBalancedWebClient = loadBalancedWebClient;
        this.outboxRepository = outboxRepository;
    }

    @Override
    @Transactional
    public Mono<OperationResultDto> transferCash(String login, BigDecimal value, String recipient) {
        LOG.info("Transfer -> Accounts. Отправка запроса на перевод наличных");
        return loadBalancedWebClient.post()
                .uri(uriBuilder -> buildUri(uriBuilder, value, login, recipient))
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

    private URI buildUri(UriBuilder uriBuilder, BigDecimal value, String login, String recipient) {
        return uriBuilder
                .scheme(SCHEME)
                .host(accountsHost)
                .port(accountsPort)
                .path(TRANSFER_PATH)
                .queryParam(LOGIN_PARAMETER, login)
                .queryParam(VALUE_PARAMETER, value)
                .queryParam(RECIPIENT_PARAMETER, recipient)
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
