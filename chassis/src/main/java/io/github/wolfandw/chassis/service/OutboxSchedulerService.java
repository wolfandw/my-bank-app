package io.github.wolfandw.chassis.service;

/**
 * Планировщик исходящих сообщений.
 */
public interface OutboxSchedulerService {
    /**
     * Планирует отправку не отправленных сообщений в сервис нотификаций.
     */
    void scheduleSendUnsentOutbox() ;

    /**
     * Планирует удаление отправленных сообщений.
     */
    void scheduleDeleteSentOutbox();
}
