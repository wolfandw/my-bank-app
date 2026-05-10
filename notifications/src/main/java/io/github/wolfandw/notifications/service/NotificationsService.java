package io.github.wolfandw.notifications.service;

import io.github.wolfandw.notifications.model.Notification;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Сервис нотификаций.
 */
public interface NotificationsService {
    /**
     * Осуществляет отправку не отправленных нотификаций.
     */
    Flux<Notification> processSendUnsentNotifications() ;

    /**
     * Осуществляет удаление отправленных нотификаций.
     */
    Mono<Void> processDeleteSentNotifications();
}
