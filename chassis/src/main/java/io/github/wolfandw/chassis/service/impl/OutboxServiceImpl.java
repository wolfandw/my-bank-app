package io.github.wolfandw.chassis.service.impl;

import io.github.wolfandw.chassis.model.Outbox;
import io.github.wolfandw.chassis.repository.OutboxRepository;
import io.github.wolfandw.chassis.service.OutboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

/**
 * Реализация {@link OutboxService}.
 */
public class OutboxServiceImpl implements OutboxService {
    private static final Logger LOG = LoggerFactory.getLogger(OutboxServiceImpl.class);

    private static final String SCHEME = "http";
    private static final String NOTIFY_PATH = "/api/notifications";
    private static final String OUTBOX_ID_PARAMETER = "outboxId";
    private static final String USER_ID_PARAMETER = "userId";
    private static final String MESSAGE_PARAMETER = "message";

    private final String NOTIFICATIONS_API_UNAVAILABLE = "Сервис нотификаций недоступен: %s";

    private final WebClient loadBalancedWebClient;
    private final OutboxRepository outboxRepository;

    @Value("${notifications.host}")
    private String notificationsHost;

    @Value("${notifications.port}")
    private String notificationsPort;

    /**
     * Создает сервис.
     *
     * @param loadBalancedWebClient load-balanced веб-клиент
     * @param outboxRepository репозиторий сообщений
     */
    public OutboxServiceImpl(WebClient loadBalancedWebClient, OutboxRepository outboxRepository) {
        this.loadBalancedWebClient = loadBalancedWebClient;
        this.outboxRepository = outboxRepository;
    }

    @Scheduled(fixedDelayString = "PT3s")
    @Transactional
    public Flux<Outbox> sendUnsentOutbox() {
        return outboxRepository.findAllBySent(false).flatMap(this::sendOutbox);
    }

    @Scheduled(fixedDelayString = "PT10s")
    @Transactional
    public Mono<Void> deleteSentOutbox() {
        return outboxRepository.deleteAllBySent(true);
    }

    private Mono<Outbox> sendOutbox(Outbox outbox) {
        LOG.info("Outbox -> Notifications. Отправка запроса на нотификацию " + outbox.getMessage());
        return loadBalancedWebClient.post()
                .uri(uriBuilder -> buildUri(uriBuilder, outbox.getId(), outbox.getUserId(), outbox.getMessage()))
                .retrieve()
                .bodyToMono(UUID.class)
                .flatMap(this::markSent)
                .onErrorResume(e -> {
                    String errorMessage = NOTIFICATIONS_API_UNAVAILABLE.formatted(e.getMessage());
                    LOG.error(errorMessage, e);
                    return Mono.empty();
                });
    }

    private Mono<Outbox> markSent(UUID sentOutboxId) {
        LOG.info("Notifications -> Outbox. Запрос на нотификацию принят");
        return outboxRepository.findById(sentOutboxId).flatMap(outbox -> {
            outbox.setSent(true);
            return outboxRepository.save(outbox);
        });
    }

    private URI buildUri(UriBuilder uriBuilder, UUID outboxId, UUID userId, String message) {
        return uriBuilder
                .scheme(SCHEME)
                .host(notificationsHost)
                .port(notificationsPort)
                .path(NOTIFY_PATH)
                .queryParam(OUTBOX_ID_PARAMETER, outboxId)
                .queryParam(USER_ID_PARAMETER, userId)
                .queryParam(MESSAGE_PARAMETER, message)
                .build();
    }
}
