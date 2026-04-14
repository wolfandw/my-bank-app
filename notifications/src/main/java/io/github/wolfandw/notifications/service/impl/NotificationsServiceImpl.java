package io.github.wolfandw.notifications.service.impl;

import io.github.wolfandw.notifications.model.Notification;
import io.github.wolfandw.notifications.repository.NotificationsRepository;
import io.github.wolfandw.notifications.service.NotificationsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Реализация {@link NotificationsService}
 */
@Service
public class NotificationsServiceImpl implements NotificationsService {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationsServiceImpl.class);

    private final NotificationsRepository notificationsRepository;

    /**
     * Создает сервис.
     *
     * @param notificationsRepository репозиторий нотификаций
     */
    public NotificationsServiceImpl(NotificationsRepository notificationsRepository) {
        this.notificationsRepository = notificationsRepository;
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('NOTIFICATIONS_SERVICE_CLIENT')")
    public Mono<String> requestNotification(UUID outboxId, UUID userId, String message) {
        LOG.debug("Notifications. Обрабатывается запрос на отправку уведомления");
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setOutboxId(outboxId);
        notification.setMessage(message);
        return notificationsRepository.save(notification).flatMap(savedNotification -> {
            LOG.debug("Notifications. Уведомление на отправку принято: '{}'", savedNotification.getMessage());
            savedNotification.setSent(true);
            return notificationsRepository.save(savedNotification).map(sentNotification -> {
                LOG.debug("Notifications. Уведомление отправлено: '{}'", savedNotification.getMessage());

                // собственно отправка уведомления
                System.out.println("****************");
                System.out.println("* Notification * - Уведомление отправлено: " + savedNotification.getMessage());
                System.out.println("****************");

                return notificationsRepository.delete(sentNotification);
            }).thenReturn(outboxId.toString());
        });
    }
}
