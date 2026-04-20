package io.github.wolfandw.chassis.service;

import io.github.wolfandw.chassis.model.Outbox;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Сервис исходящих сообщений.
 */
public interface OutboxProcessorService {
    /**
     * Осуществляет отправку не отправленных сообщений в сервис нотификаций.
     */
    Flux<Outbox> processSendingUnsentOutbox() ;

    /**
     * Осуществляет удаление отправленных сообщений.
     */
    Mono<Void> processDeletingSentOutbox();
}
