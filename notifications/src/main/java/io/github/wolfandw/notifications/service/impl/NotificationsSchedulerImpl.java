package io.github.wolfandw.notifications.service.impl;

import io.github.wolfandw.notifications.service.NotificationsScheduler;
import io.github.wolfandw.notifications.service.NotificationsService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Реализация {@link NotificationsService}
 */
@Service
public class NotificationsSchedulerImpl implements NotificationsScheduler {
    private final NotificationsService notificationsService;

    /**
     * Создает планировщик.
     *
     * @param notificationsService обработчик отправки нотификаций
     */
    public NotificationsSchedulerImpl(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    @Scheduled(fixedDelayString = "PT3s")
    @Override
    public void scheduleSendUnsentNotifications() {
        notificationsService.processSendUnsentNotifications().subscribe();
    }

    @Scheduled(fixedDelayString = "PT10s")
    @Override
    public void scheduleDeleteSentNotifications() {
        notificationsService.processDeleteSentNotifications().subscribe();
    }
}
