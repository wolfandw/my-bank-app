package io.github.wolfandw.notifications.test.service;

import io.github.wolfandw.notifications.model.Notification;
import io.github.wolfandw.notifications.repository.NotificationsRepository;
import io.github.wolfandw.notifications.service.impl.NotificationsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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

    @Test
    void requestNotificationIsUnauthorizedTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        when(notificationsRepository.save(any(Notification.class)))
                .thenReturn(Mono.error(new AuthorizationDeniedException("error")));
        StepVerifier.create(notificationsService.requestNotification(outboxId, outboxId, "test message"))
                .verifyError(AuthorizationDeniedException.class);
    }

    @Test
    void requestNotificationIsEmptyTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        when(notificationsRepository.save(any(Notification.class)))
                .thenReturn(Mono.empty());
        StepVerifier.create(notificationsService.requestNotification(outboxId, outboxId, "test message"))
                .verifyComplete();
    }

    @Test
    @WithMockUser(roles = "NOTIFICATIONS_SERVICE_CLIENT")
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
                    assertThat(actualOutboxId).isEqualTo(outboxId.toString());
                }).verifyComplete();
    }
}
