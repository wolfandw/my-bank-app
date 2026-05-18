package io.github.wolfandw.notifications.itest.repository;

import io.github.wolfandw.notifications.itest.BaseNotificationsIntegrationTest;
import io.github.wolfandw.notifications.model.Notification;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты репозитория уведомлений.
 */
public class NotificationsRepositoryIntegrationTest extends BaseNotificationsIntegrationTest {
    @BeforeEach
    void setUp() {
        notificationsRepository.deleteAll()
                .thenMany(Flux.fromIterable(getTestNotifications()))
                .concatMap(outbox -> notificationsRepository.save(outbox))
                .delaySubscription(Duration.ofMillis(50))
                .blockLast();
    }

    @Test
    void findAllBySentFalseTest() {
        StepVerifier.create(notificationsRepository.findAllBySent(false).collectList()).
                assertNext(actualNotifications -> {
                    assertThat(actualNotifications).size().isEqualTo(3);
                    Assertions.assertThat(actualNotifications.get(0).getSent()).isFalse();
                    Assertions.assertThat(actualNotifications.get(1).getSent()).isFalse();
                    Assertions.assertThat(actualNotifications.get(2).getSent()).isFalse();
                }).verifyComplete();
    }

    @Test
    void findAllBySentTrueTest() {
        StepVerifier.create(notificationsRepository.findAllBySent(true).collectList()).
                assertNext(actualNotifications -> {
                    assertThat(actualNotifications).size().isEqualTo(3);
                    Assertions.assertThat(actualNotifications.get(0).getSent()).isTrue();
                    Assertions.assertThat(actualNotifications.get(1).getSent()).isTrue();
                    Assertions.assertThat(actualNotifications.get(2).getSent()).isTrue();
                }).verifyComplete();
    }

    @Test
    void deleteAllBySentTest() {
        StepVerifier.create(notificationsRepository.deleteAllBySent(true)).expectNextCount(0).verifyComplete();
    }

    @Test
    void findByOutboxIdAndSentTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Notification notification = new Notification();
        notification.setUserId("useer");
        notification.setOutboxId(outboxId);
        notification.setSent(false);
        notification.setMessage("test message");

        StepVerifier.create(notificationsRepository.save(notification)).expectNextCount(1).verifyComplete();
        StepVerifier.create(notificationsRepository.findByOutboxIdAndSent(outboxId,false)).assertNext(actualNotification -> {
            assertThat(actualNotification.getOutboxId()).isEqualTo(outboxId);
        }).verifyComplete();

        notification = new Notification();
        notification.setUserId("user");
        notification.setOutboxId(outboxId);
        notification.setSent(true);
        notification.setMessage("test message");
        StepVerifier.create(notificationsRepository.save(notification)).expectNextCount(1).verifyComplete();
        StepVerifier.create(notificationsRepository.findByOutboxIdAndSent(outboxId,true)).assertNext(actualNotification -> {
            assertThat(actualNotification.getOutboxId()).isEqualTo(outboxId);
        }).verifyComplete();
    }
}