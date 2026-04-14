package io.github.wolfandw.notifications.itest.repository;

import io.github.wolfandw.notifications.itest.BaseNotificationsTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты репозитория уведомлений.
 */
public class NotificationsRepositoryIntegrationTest extends BaseNotificationsTest {
    @Test
    void findAllBySentFalseTest() {
        trxStepVerifier.create(notificationsRepository.findAllBySent(false).collectList()).
                assertNext(actualNotifications -> {
                    assertThat(actualNotifications).size().isEqualTo(3);
                    assertThat(actualNotifications.get(0).getOutboxId()).isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
                    assertThat(actualNotifications.get(1).getOutboxId()).isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"));
                    assertThat(actualNotifications.get(2).getOutboxId()).isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440002"));
                }).verifyComplete();
    }

    @Test
    void findAllBySentTrueTest() {
        trxStepVerifier.create(notificationsRepository.findAllBySent(true).collectList()).
                assertNext(actualNotifications -> {
                    assertThat(actualNotifications).size().isEqualTo(3);
                    assertThat(actualNotifications.get(0).getOutboxId()).isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440003"));
                    assertThat(actualNotifications.get(1).getOutboxId()).isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440004"));
                    assertThat(actualNotifications.get(2).getOutboxId()).isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440005"));
                }).verifyComplete();
    }

    @Test
    void deleteAllBySentTest() {
        trxStepVerifier.create(notificationsRepository.deleteAllBySent(true)).expectNextCount(0).verifyComplete();
    }
}