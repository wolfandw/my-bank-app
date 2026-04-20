package io.github.wolfandw.chassis.service;

import io.github.wolfandw.chassis.model.Outbox;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Планировщик исходящих сообщений.
 */
public interface OutboxSchedulerService {
    /**
     * Планирует отправку не отправленных сообщений в сервис нотификаций.
     */
    Flux<Outbox> scheduleSendUnsentOutbox() ;

    /**
     * Планирует удаление отправленных сообщений.
     */
    Mono<Void> scheduleDeleteSentOutbox();
}
