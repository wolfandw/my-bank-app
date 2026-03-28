package io.github.wolfandw.chassis.service;

import io.github.wolfandw.chassis.model.Outbox;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Сервис исходящих сообщений.
 */
public interface OutboxService {
    /**
     * Отправляет не отправленные сообщения в сервис нотификаций.
     */
    Flux<Outbox> sendUnsentOutbox() ;

    /**
     * Удаляет отправленные сообщения.
     */
    Mono<Void> deleteSentOutbox();
}
