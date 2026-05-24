package io.github.wolfandw.chassis.service.impl;

import io.github.wolfandw.chassis.service.OutboxProcessorService;
import io.github.wolfandw.chassis.service.OutboxSchedulerService;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.publisher.Mono;

/**
 * Реализация {@link OutboxSchedulerService}.
 */
public class OutboxSchedulerServiceImpl implements OutboxSchedulerService {
    private final OutboxProcessorService outboxProcessorService;
    private final ObservationRegistry observationRegistry;

    /**
     * Создает сервис.
     *
     * @param outboxProcessorService обработчик отправки сообщений
     */
    public OutboxSchedulerServiceImpl(OutboxProcessorService outboxProcessorService,
                                      ObservationRegistry observationRegistry) {
        this.outboxProcessorService = outboxProcessorService;
        this.observationRegistry = observationRegistry;
    }

    @Scheduled(fixedDelayString = "PT3s")
    @Override
    public void scheduleSendUnsentOutbox() {
        outboxProcessorService.existsUnsentOutbox()
                .flatMap(hasRecords -> {
                    if (hasRecords) {
                        Observation schedulerObs = Observation.createNotStarted(
                                "send-unsent-outbox",
                                observationRegistry
                        );
                        schedulerObs.start();

                        return outboxProcessorService.processSendingUnsentOutbox()
                                .then()
                                .doOnTerminate(schedulerObs::stop)
                                .doOnError(schedulerObs::error)
                                .contextWrite(context -> context.put(ObservationThreadLocalAccessor.KEY, schedulerObs));
                    }
                    return Mono.empty();
                })
                .subscribe();
    }

    @Scheduled(fixedDelayString = "PT10s")
    @Override
    public void scheduleDeleteSentOutbox() {
        outboxProcessorService.existsSentOutbox()
                .flatMap(hasRecords -> {
                    if (hasRecords) {
                        Observation schedulerObs = Observation.createNotStarted(
                                "delete-sent-outbox",
                                observationRegistry
                        );
                        schedulerObs.start();

                        return outboxProcessorService.processDeletingSentOutbox()
                                .then()
                                .doOnTerminate(schedulerObs::stop)
                                .doOnError(schedulerObs::error)
                                .contextWrite(context -> context.put(ObservationThreadLocalAccessor.KEY, schedulerObs));
                    }
                    return Mono.empty();
                })
                .subscribe();
    }
}
