package io.github.wolfandw.transfer.service.impl;

import io.github.wolfandw.chassis.dto.OperationResultDto;
import io.github.wolfandw.chassis.model.Outbox;
import io.github.wolfandw.chassis.repository.OutboxRepository;
import io.github.wolfandw.transfer.service.TransferService;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
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

    private static final String ACCOUNTS_API_UNAVAILABLE = "Сервис счетов недоступен: %s";

    private static final String TRACEPARENT = "traceparent";
    private static final String TRACEPARENT_FORMAT = "00-%s-%s-01";

    private final Tracer tracer;

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
     * @param tracer трассировщик
     */
    public TransferServiceImpl(WebClient webClient, OutboxRepository outboxRepository, Tracer tracer) {
        this.webClient = webClient;
        this.outboxRepository = outboxRepository;
        this.tracer = tracer;
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('USER') and hasRole('TRANSFER_WRITE')")
    public Mono<OperationResultDto> transferCash(String login, BigDecimal value, String recipient) {
        LOG.debug("Transfer -> Accounts. Отправка запроса на перевод наличных");
        return webClient.post()
                .uri(uriBuilder -> buildUri(uriBuilder, value, login, recipient))
                .header(TRACEPARENT, getTraceParent())
                .retrieve()
                .bodyToMono(OperationResultDto.class)
                .flatMap(operationResultDto -> outbox(operationResultDto).thenReturn(operationResultDto))
                .onErrorResume(e -> {
                    LOG.error(ACCOUNTS_API_UNAVAILABLE.formatted(e.getMessage()), e);
                    return Mono.error(e);
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
        outbox.setUserId(login);
        outbox.setMessage(createMessage(login, message));
        return outbox;
    }

    private String createMessage(String login, String message) {
        return message + ", пользователь: " + login;
    }

    private String getTraceParent() {
        Span currentSpan = tracer.currentSpan();
        return String.format(TRACEPARENT_FORMAT,
                currentSpan != null ? currentSpan.context().traceId() : "",
                currentSpan != null ? currentSpan.context().spanId() : "");
    }
}
