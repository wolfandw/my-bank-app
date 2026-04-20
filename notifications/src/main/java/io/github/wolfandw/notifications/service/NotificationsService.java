package io.github.wolfandw.notifications.service;

import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Сервис нотификаций.
 */
public interface NotificationsService {
    /**
     * Запрашивает нотификацию.
     *
     * @param outboxId идентификатор исходящего сообщения
     * @param userId идентификатор получателя
     * @param message сообщение
     * @return идентификатор и признак успешной регистрации сообщения
     */
    Mono<String> requestNotification(UUID outboxId, UUID userId, String message);
}
