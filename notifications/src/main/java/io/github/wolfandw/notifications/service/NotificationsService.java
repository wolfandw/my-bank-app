package io.github.wolfandw.notifications.service;

import io.github.wolfandw.chassis.dto.AccountPageDto;
import reactor.core.publisher.Mono;

/**
 * Сервис нотификаций.
 */
public interface NotificationsService {
    /**
     * Запрашивает нотификацию.
     *
     * @return нотификация
     */
    Mono<String> requestNotification(AccountPageDto accountPageDto);

    /**
     * Отправляет нотификацию.
     *
     * @return нотификация
     */
    Mono<String> sendNotification();
}
