package io.github.wolfandw.notifications.service;

/**
 * Сервис нотификаций.
 */
public interface NotificationsScheduler {
    /**
     * Планирует отправку не отправленных нотификаций.
     */
    void scheduleSendUnsentNotifications() ;

    /**
     * Планирует удаление отправленных нотификаций.
     */
    void scheduleDeleteSentNotifications();
}
