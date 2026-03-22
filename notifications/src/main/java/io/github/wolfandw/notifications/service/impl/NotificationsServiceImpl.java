package io.github.wolfandw.notifications.service.impl;

import io.github.wolfandw.chassis.dto.AccountPageDto;
import io.github.wolfandw.notifications.service.NotificationsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Реализация {@link NotificationsService}
 */
@Service
public class NotificationsServiceImpl implements NotificationsService {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationsServiceImpl.class);

    /**
     * Создает сервис.
     */
    public NotificationsServiceImpl() {}

    @Override
    public Mono<String> requestNotification(AccountPageDto accountPageDto) {
        LOG.info("Notifications. Обрабатывается запрос на отправку уведомления");
        return Mono.just("Уведомление запрошено");
    }

    @Override
    public Mono<String> sendNotification() {
        LOG.info("Notifications. Обрабатывается отправка уведомления");
        return Mono.just("Уведомление отправлено");
    }
}
