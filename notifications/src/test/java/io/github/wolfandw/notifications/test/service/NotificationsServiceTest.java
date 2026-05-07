package io.github.wolfandw.notifications.test.service;

import io.github.wolfandw.chassis.model.Outbox;
import io.github.wolfandw.notifications.model.Notification;
import io.github.wolfandw.notifications.repository.NotificationsRepository;
import io.github.wolfandw.notifications.service.impl.NotificationsServiceImpl;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.test.hamcrest.KafkaMatchers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Модульный тест сервиса уведомлений.
 */
@ExtendWith(MockitoExtension.class)
public class NotificationsServiceTest {
    @Mock
    private NotificationsRepository notificationsRepository;

    @InjectMocks
    private NotificationsServiceImpl notificationsService;

    @ParameterizedTest
    @ValueSource(strings = {"accounts-to-notifications", "cash-to-notifications", "transfer-to-notifications"})
    void consumerNotificationTest(String topic) {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Outbox outbox = new Outbox();
        outbox.setId(outboxId);
        outbox.setUserId(outboxId);
        outbox.setMessage("test message, топик: " + topic);

        try (MockConsumer<UUID, Outbox> mockConsumer = new MockConsumer<>("earliest")) {
            TopicPartition partition = new TopicPartition(topic, 0);
            mockConsumer.assign(Collections.singletonList(partition));
            mockConsumer.updateBeginningOffsets(Map.of(partition, 0L));
            mockConsumer.addRecord(new ConsumerRecord<>(topic, 0, 0L, outboxId, outbox));

            ConsumerRecord<UUID, Outbox> record = mockConsumer.poll(Duration.ofMillis(100)).iterator().next();

            MatcherAssert.assertThat(record, KafkaMatchers.hasKey(outboxId));
            MatcherAssert.assertThat(record, KafkaMatchers.hasValue(outbox));
        }
    }

    @Test
    void requestNotificationTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Notification notification = new Notification();
        notification.setUserId(outboxId);
        notification.setOutboxId(outboxId);
        notification.setMessage("test message");
        when(notificationsRepository.save(any(Notification.class)))
                .thenReturn(Mono.just(notification));
        when(notificationsRepository.delete(any(Notification.class)))
                .thenReturn(Mono.empty());
        StepVerifier.create(notificationsService.requestNotification(outboxId, outboxId, "test message")).
                consumeNextWith(actualOutboxId -> {
                    assertThat(actualOutboxId).isEqualTo(outboxId);
                }).verifyComplete();
    }
}
