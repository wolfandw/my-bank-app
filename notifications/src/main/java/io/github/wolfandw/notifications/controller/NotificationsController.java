package io.github.wolfandw.notifications.controller;

import io.github.wolfandw.chassis.dto.AccountPageDto;
import io.github.wolfandw.notifications.service.NotificationsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

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
    @PostMapping("/api/notify")
    public Mono<String> requestNotification( @RequestBody Mono<AccountPageDto> accountPageDtoMono) {
        LOG.info("Сервис -> Notifications. Получен запрос на нотификацию");
        return accountPageDtoMono.flatMap(notificationsService::requestNotification);
    }
}
