package io.github.wolfandw.notifications.service.impl;

import io.github.wolfandw.notifications.service.NotificationsScheduler;
import io.github.wolfandw.notifications.service.NotificationsService;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Реализация {@link NotificationsService}
 */
@Service
public class NotificationsSchedulerImpl implements NotificationsScheduler {
    private final NotificationsService notificationsService;
    private final ObservationRegistry observationRegistry;

    /**
     * Создает планировщик.
     *
     * @param notificationsService обработчик отправки нотификаций
     */
    public NotificationsSchedulerImpl(NotificationsService notificationsService,
                                      ObservationRegistry observationRegistry) {
        this.notificationsService = notificationsService;
        this.observationRegistry = observationRegistry;
    }

    @Scheduled(fixedDelayString = "PT3s")
    @Override
    public void scheduleSendUnsentNotifications() {
        notificationsService.existsUnsentNotifications()
                .flatMap(hasRecords -> {
                    if (hasRecords) {
                        Observation schedulerObs = Observation.createNotStarted(
                                "send-unsent-notification",
                                observationRegistry
                        );
                        schedulerObs.start();

                        return notificationsService.processSendUnsentNotifications()
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
    public void scheduleDeleteSentNotifications() {
        notificationsService.existsSentNotifications()
                .flatMap(hasRecords -> {
                    if (hasRecords) {
                        Observation schedulerObs = Observation.createNotStarted(
                                "delete-sent-notification",
                                observationRegistry
                        );
                        schedulerObs.start();

                        return notificationsService.processDeleteSentNotifications()
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
