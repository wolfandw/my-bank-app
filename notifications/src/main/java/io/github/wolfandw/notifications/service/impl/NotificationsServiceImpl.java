package io.github.wolfandw.notifications.service.impl;

import io.github.wolfandw.chassis.model.Outbox;
import io.github.wolfandw.notifications.model.Notification;
import io.github.wolfandw.notifications.repository.NotificationsRepository;
import io.github.wolfandw.notifications.service.NotificationsService;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

import java.util.UUID;

/**
 * Реализация {@link NotificationsService}
 */
@Service
public class NotificationsServiceImpl implements NotificationsService {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationsServiceImpl.class);

    private final NotificationsRepository notificationsRepository;
    private final KafkaSender<UUID, Outbox> kafkaSender;
    @Value("${spring.kafka.topics.topic")
    private String topic;

    /**
     * Создает сервис.
     *
     * @param notificationsRepository репозиторий нотификаций
     */
    public NotificationsServiceImpl(NotificationsRepository notificationsRepository,
                                    KafkaSender<UUID, Outbox> kafkaSender) {
        this.notificationsRepository = notificationsRepository;
        this.kafkaSender = kafkaSender;
    }

    @KafkaListener(topics = {
            "${ACCOUNTS_TO_NOTIFICATIONS_KAFKA_TOPIC}",
            "${CASH_TO_NOTIFICATIONS_KAFKA_TOPIC:cash-to-notifications}",
            "${TRANSFER_TO_NOTIFICATIONS_KAFKA_TOPIC:transfer-to-notifications}"
    }, groupId = "${KAFKA_CONSUMER_GROUP_ID}")
    public Flux<SenderResult<UUID>> processExternal(Outbox outbox) {
        LOG.debug("Outbox -> Notifications. Получен запрос на нотификацию");
        SenderRecord<UUID, Outbox, UUID> record =
                SenderRecord.create(new ProducerRecord<>(topic, outbox.getId(), outbox), outbox.getId());
        return kafkaSender.send(Flux.just(record));
    }

    @KafkaListener(topics = {"${NOTIFICATIONS_TO_NOTIFICATIONS_KAFKA_TOPIC}"},
            groupId = "${KAFKA_CONSUMER_GROUP_ID}")
    public Mono<UUID> processInternal(Outbox outbox) {
        LOG.debug("Notifications -> Notifications. Получен запрос на нотификацию");
        return requestNotification(outbox.getId(),outbox.getUserId(), outbox.getMessage());
    }

    @Override
    @Transactional
    public Mono<UUID> requestNotification(UUID outboxId, UUID userId, String message) {
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
            }).thenReturn(outboxId);
        });
    }
}
