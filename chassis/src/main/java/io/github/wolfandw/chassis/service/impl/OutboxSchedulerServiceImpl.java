package io.github.wolfandw.chassis.service.impl;

import io.github.wolfandw.chassis.service.OutboxProcessorService;
import io.github.wolfandw.chassis.service.OutboxSchedulerService;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Реализация {@link OutboxSchedulerService}.
 */
public class OutboxSchedulerServiceImpl implements OutboxSchedulerService {
    private final OutboxProcessorService outboxProcessorService;

    /**
     * Создает сервис.
     *
     * @param outboxProcessorService обработчик отправки сообщений
     */
    public OutboxSchedulerServiceImpl(OutboxProcessorService outboxProcessorService) {
        this.outboxProcessorService = outboxProcessorService;
    }

    @Scheduled(fixedDelayString = "PT3s")
    @Override
    public void scheduleSendUnsentOutbox() {
        outboxProcessorService.processSendingUnsentOutbox().subscribe();
    }

    @Scheduled(fixedDelayString = "PT10s")
    @Override
    public void scheduleDeleteSentOutbox() {
        outboxProcessorService.processDeletingSentOutbox().subscribe();
    }
}
