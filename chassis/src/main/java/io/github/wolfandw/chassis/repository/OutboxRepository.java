package io.github.wolfandw.chassis.repository;

import io.github.wolfandw.chassis.model.Outbox;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Репозиторий исходящих сообщений.
 */
@Repository
public interface OutboxRepository extends R2dbcRepository<Outbox, UUID> {
    /**
     * Возвращает список сообщений по указанному признаку отправления.
     *
     * @param sent признак отправленного сообщения
     * @return список сообщений с указанным признаком
     */
    Flux<Outbox> findAllBySent(boolean sent);

    /**
     * Удаляет список сообщений по указанному признаку отправления.
     *
     * @param sent признак отправленного сообщения
     * @return ничего
     */
    Mono<Void> deleteAllBySent(boolean sent);
}
