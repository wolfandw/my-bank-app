package io.github.wolfandw.notifications.test.controller;

import io.github.wolfandw.notifications.controller.NotificationsController;
import io.github.wolfandw.notifications.service.NotificationsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Модульные тесты контроллера уведомлений.
 */
@ExtendWith(MockitoExtension.class)
class NotificationsControllerTest {
    @Mock
    private NotificationsService notificationsService;

    @InjectMocks
    private NotificationsController notificationsController;

    @Test
    void requestNotificationIsUnauthorizedTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        when(notificationsService.requestNotification(any(UUID.class), any(UUID.class), any(String.class)))
                .thenReturn(Mono.error(new RuntimeException("error")));

        StepVerifier.create(notificationsController.requestNotification(outboxId, outboxId, "test message")).
                expectErrorMessage("error").verify();
    }

    @Test
    void requestNotificationIsEmptyTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        when(notificationsService.requestNotification(any(UUID.class), any(UUID.class), any(String.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(notificationsController.requestNotification(outboxId, outboxId, "test message"))
                .verifyComplete();
    }

    @Test
    void requestNotificationTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        when(notificationsService.requestNotification(any(UUID.class), any(UUID.class), any(String.class)))
                .thenReturn(Mono.just(outboxId.toString()));

        StepVerifier.create(notificationsController.requestNotification(outboxId, outboxId, "test message"))
                .expectNextMatches(actualOutboxId -> actualOutboxId.equals(outboxId.toString()))
                .verifyComplete();
    }
}
