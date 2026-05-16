package io.github.wolfandw.notifications.service.impl;

import io.github.wolfandw.chassis.model.Outbox;
import io.github.wolfandw.notifications.model.Notification;
import io.github.wolfandw.notifications.repository.NotificationsRepository;
import io.github.wolfandw.notifications.service.NotificationsService;
import io.micrometer.core.instrument.Counter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Реализация {@link NotificationsService}
 */
@Service
public class NotificationsServiceImpl implements NotificationsService {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationsServiceImpl.class);

    private final NotificationsRepository notificationsRepository;
    private final Counter sendUnsentNotificationSuccessCounter;
    private final Counter sendUnsentNotificationFailureCounter;

    /**
     * Создает сервис.
     *
     * @param notificationsRepository репозиторий нотификаций
     * @param sendUnsentNotificationSuccessCounter счетчик отправленных нотификаций
     * @param sendUnsentNotificationFailureCounter счетчик не отправленных нотификаций
     */
    public NotificationsServiceImpl(NotificationsRepository notificationsRepository,
                                    Counter sendUnsentNotificationSuccessCounter,
                                    Counter sendUnsentNotificationFailureCounter) {
        this.notificationsRepository = notificationsRepository;
        this.sendUnsentNotificationSuccessCounter = sendUnsentNotificationSuccessCounter;
        this.sendUnsentNotificationFailureCounter = sendUnsentNotificationFailureCounter;
    }

    @KafkaListener(topics = {"${accounts.kafka.topic}",
                             "${cash.kafka.topic}",
                             "${transfer.kafka.topic}"},
                   groupId = "${notifications.kafka.consumer.group-id}",
                   containerFactory = "kafkaListenerContainerFactory")
    public void listen(ConsumerRecord<UUID, Outbox> record) {
        LOG.debug("Outbox -> Notifications. Получен запрос на нотификацию, топик = " + record.topic());
        requestNotification(record.value().getId(), record.value().getUserId(), record.value().getMessage())
                .contextCapture().block();
    }

    @Override
    public Flux<Notification> processSendUnsentNotifications() {
        return notificationsRepository.findAllBySent(false).flatMap(this::sendNotification).contextCapture();
    }

    @Override
    public Mono<Void> processDeleteSentNotifications() {
        return notificationsRepository.deleteAllBySent(true).contextCapture();
    }


    @Override
    public Mono<Boolean> existsUnsentNotifications() {
        return notificationsRepository.existsBySent(false);
    }

    @Override
    public Mono<Boolean> existsSentNotifications() {
        return notificationsRepository.existsBySent(true);
    }

    private Mono<Notification> requestNotification(UUID outboxId, UUID userId, String message) {
        LOG.debug("Notifications. Обрабатывается запрос на отправку уведомления");
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setOutboxId(outboxId);
        notification.setMessage(message);
        return notificationsRepository.save(notification);
    }

    private Mono<Notification> sendNotification(Notification notification) {
        LOG.debug("Notifications. Отправка нотификации " + notification.getMessage());
        notification.setSent(true);
        return notificationsRepository.save(notification).map(sentNotification -> {
            // собственно отправка уведомлени
            sendUnsentNotificationSuccessCounter.increment();
            LOG.info("Notifications. Уведомление отправлено: '{}'", sentNotification.getMessage());
            return sentNotification;
        })
        .onErrorResume(e -> {
            sendUnsentNotificationFailureCounter.increment();
            LOG.error("Notifications. Уведомление НЕ отправлено: '{}'", notification.getMessage(), e);
            return Mono.empty();
        });
    }
}
