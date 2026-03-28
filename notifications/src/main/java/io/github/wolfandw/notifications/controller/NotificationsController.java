package io.github.wolfandw.notifications.controller;

import io.github.wolfandw.notifications.service.NotificationsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
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
     * @return нотификация текущего пользователя
     */
    @PostMapping("/api/notifications")
    public Mono<UUID> requestNotification(@RequestParam(value = "outboxId", required = false) UUID outboxId,
                                          @RequestParam(value = "userId", required = false) UUID userId,
                                          @RequestParam(value = "message", required = false) String message) {
        LOG.info("Outbox -> Notifications. Получен запрос на нотификацию");
        return notificationsService.requestNotification(outboxId, userId, message);
    }
}
