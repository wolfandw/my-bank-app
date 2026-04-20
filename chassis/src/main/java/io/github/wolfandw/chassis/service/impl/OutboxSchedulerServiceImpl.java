package io.github.wolfandw.chassis.service.impl;

import io.github.wolfandw.chassis.model.Outbox;
import io.github.wolfandw.chassis.service.OutboxProcessorService;
import io.github.wolfandw.chassis.service.OutboxSchedulerService;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Flux<Outbox> scheduleSendUnsentOutbox() {
        return outboxProcessorService.processSendingUnsentOutbox();
    }

    @Scheduled(fixedDelayString = "PT10s")
    @Override
    public Mono<Void> scheduleDeleteSentOutbox() {
        return outboxProcessorService.processDeletingSentOutbox();
    }
}
