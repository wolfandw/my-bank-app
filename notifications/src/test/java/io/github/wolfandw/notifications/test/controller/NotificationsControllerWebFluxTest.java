package io.github.wolfandw.notifications.test.controller;

import io.github.wolfandw.notifications.controller.NotificationsController;
import io.github.wolfandw.notifications.repository.NotificationsRepository;
import io.github.wolfandw.notifications.service.NotificationsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

/**
 * Модульные тесты контроллера уведомлений.
 */
@WebFluxTest(NotificationsController.class)
public class NotificationsControllerWebFluxTest {
    @MockitoBean(reset = MockReset.BEFORE)
    private NotificationsService notificationsService;

    @MockitoBean(reset = MockReset.BEFORE)
    private NotificationsRepository notificationsRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void requestNotificationIsUnauthorizedTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/notifications")
                        .queryParam("outboxId", outboxId)
                        .queryParam("userId", outboxId)
                        .queryParam("message", "test")
                        .build())
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @WithMockUser(roles = "NOTIFICATIONS_SERVICE_CLIENT")
    void requestNotificationTest() {
        UUID outboxId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        when(notificationsService.requestNotification(any(UUID.class), any(UUID.class), any(String.class)))
                .thenReturn(Mono.just(outboxId.toString()));

        webTestClient.mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/notifications")
                        .queryParam("outboxId", outboxId)
                        .queryParam("userId", outboxId)
                        .queryParam("message", "test")
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(actualOutboxId -> {
                    assertThat(actualOutboxId).isEqualTo(outboxId.toString());
                });
    }
}
