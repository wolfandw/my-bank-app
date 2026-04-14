package io.github.wolfandw.notifications.itest.service;

import io.github.wolfandw.notifications.itest.BaseNotificationsTest;
import org.junit.jupiter.api.Test;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Интеграционный тест сервиса уведомлений.
 */
public class NotificationsServiceIntegrationTest extends BaseNotificationsTest {

    @Test
    void requestNotificationIsUnauthorizedTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        trxStepVerifier.create(notificationsService.requestNotification(outboxId, outboxId, "test message"))
                .verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "NOTIFICATIONS_SERVICE_CLIENT")
    void requestNotificationTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        trxStepVerifier.create(notificationsService.requestNotification(outboxId, outboxId, "test message")).
                consumeNextWith(actualOutboxId -> {
                    assertThat(actualOutboxId).isEqualTo(outboxId.toString());
                }).verifyComplete();
    }
}
