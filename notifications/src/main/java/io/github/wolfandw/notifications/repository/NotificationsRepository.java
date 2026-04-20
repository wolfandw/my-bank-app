package io.github.wolfandw.notifications.repository;

import io.github.wolfandw.notifications.model.Notification;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Репозиторий исходящих сообщений.
 */
public interface NotificationsRepository extends R2dbcRepository<Notification, UUID> {
    /**
     * Возвращает список сообщений по указанному признаку отправления.
     *
     * @param sent признак отправленного сообщения
     * @return список сообщений с указанным признаком
     */
    Flux<Notification> findAllBySent(boolean sent);

    /**
     * Удаляет список сообщений по указанному признаку отправления.
     *
     * @param sent признак отправленного сообщения
     * @return ничего
     */
    Mono<Void> deleteAllBySent(boolean sent);
}
