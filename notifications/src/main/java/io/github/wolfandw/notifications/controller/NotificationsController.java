package io.github.wolfandw.notifications.controller;

import io.github.wolfandw.notifications.service.NotificationsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Контроллер для работы с нотификациями.
 */
@RestController
public class NotificationsController {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationsController.class);

    private final NotificationsService notificationsService;

    /**
     * Создает контроллер для работы с нотификациями.
     *
     * @param notificationsService сервис нотификаций
     */
    public NotificationsController(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    /**
     * Осуществляет перевод получателю.
     *
     * @param outboxId ижентификатор сообщения
     * @param userId идентификатор пользователя
     * @param message сообщение
     * @return нотификация текущего пользователя
     */
    @PostMapping("/api/notifications")
    @PreAuthorize("hasRole('NOTIFICATIONS_SERVICE_CLIENT')")
    public Mono<String> requestNotification(@RequestParam(value = "outboxId", required = false) UUID outboxId,
                                          @RequestParam(value = "userId", required = false) UUID userId,
                                          @RequestParam(value = "message", required = false) String message) {
        LOG.debug("Outbox -> Notifications. Получен запрос на нотификацию");
        return notificationsService.requestNotification(outboxId, userId, message);
    }
}
